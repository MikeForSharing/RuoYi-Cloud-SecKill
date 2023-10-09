package com.ruoyi.seckill.service.imp;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.alipay.api.RemoteAlipayService;
import com.ruoyi.alipay.api.model.AlipayVo;
import com.ruoyi.alipay.api.model.RefundVo;
import com.ruoyi.common.core.constant.RabbitConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.uuid.IdUtils;
import com.ruoyi.common.redis.enums.SeckillRedisKey;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.intergral.api.RemoteIntergralService;
import com.ruoyi.intergral.api.model.OperateIntergralVo;
import com.ruoyi.seckill.api.model.*;
import com.ruoyi.seckill.constant.SeckillCodeMsg;
import com.ruoyi.seckill.exception.SeckillException;
import com.ruoyi.seckill.mapper.OrderInfoMapper;
import com.ruoyi.seckill.mapper.PayLogMapper;
import com.ruoyi.seckill.mapper.SeckillProductMapper;
import com.ruoyi.seckill.service.ISeckillOrderService;
import com.ruoyi.seckill.service.ISeckillProductService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangJiang
 */
@Service
public class SeckillOrderSeviceImpl implements ISeckillOrderService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private SeckillProductMapper seckillProductMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    @Lazy
    private ISeckillProductService seckillProductService;


    @Autowired
    private RemoteAlipayService remoteAlipayService;


    @Autowired
    private RemoteIntergralService remoteIntergralService;

    @Autowired
    private PayLogMapper payLogMapper;

    @Value("${pay.returnUrl}")
    private String returlUrl;
    @Value("${pay.notifyUrl}")
    private String notifyUrl;

    @Override
    public String createOrderInfo(String userId, SeckillProductVo seckillProductVo) {
        OrderInfo info = new OrderInfo();
        info.setCreateDate(new Date());
        info.setSeckillDate(seckillProductVo.getStartDate());
        info.setSeckillId(seckillProductVo.getId());
        info.setIntergral(seckillProductVo.getIntergral());
        info.setProductId(seckillProductVo.getProductId());
        info.setProductImg(seckillProductVo.getProductImg());
        info.setProductName(seckillProductVo.getProductName());
        info.setProductPrice(seckillProductVo.getProductPrice());
        info.setSeckillPrice(seckillProductVo.getSeckillPrice());
        info.setSeckillTime(seckillProductVo.getTime());
        info.setUserId(Long.parseLong(userId));
        info.setOrderNo(IdUtils.fastUUID());


        orderInfoMapper.insert(info);
//        String key = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(seckillProductVo.getId()));
//        redisService.addCacheSet(key, userId);
        return info.getOrderNo();
    }

    @Override
    @Transactional
    public String doSeckill(String userId, SeckillProductVo vo) {
        int count = seckillProductMapper.decrStock(vo.getId());
        if (count == 0) { //若影响行数为0，说明库存为0，从而避免超卖
            throw new SeckillException("商品已卖完！");
        }
        //创建秒杀订单(使用数据库唯一索引保证用户不会重复下单)
        String orderNo = createOrderInfo(userId, vo);
        return orderNo;
    }

    @Override
    public OrderInfo selectOrderById(String orderNo) {
        //从数据库查询
//        return orderInfoMapper.find(orderNo);
        OrderInfo orderInfo;
        //从redis中获取订单信息
        String orderHashKey = SeckillRedisKey.SECKILL_ORDER_HASH.getRealKey("");
        String objStr = redisService.getCacheMapValue(orderHashKey, orderNo);
        if (!objStr.isEmpty()){
            orderInfo = JSONObject.parseObject(objStr, OrderInfo.class);
        }else {
            //防止缓存击穿-利用Redis实现分布式锁
            String lockKey = "lock"+orderNo;
            for (;;){
                if (redisService.setCacheObjectIfAbsent(lockKey, System.currentTimeMillis(), RabbitConstants.REDIS_MESSAGEID_EXPIRATION, TimeUnit.SECONDS)) {
                    //加锁成功的线程，再次从redis中检查数据是否存在
                    objStr = redisService.getCacheMapValue(orderHashKey, orderNo);
                    if (!objStr.isEmpty()){
                        orderInfo = JSONObject.parseObject(objStr, OrderInfo.class);
                        //释放锁
                        redisService.deleteObject(lockKey);
                    }else {
                        //从数据库中查询
                        // TODO: 2023/9/11 看看查询不到的清空下，看看返回结果是什么
                        orderInfo = orderInfoMapper.find(orderNo);
                        if (orderInfo == null){
                            //防止缓存穿透-若数据库中也查询不到该time场次，将此time值也加入redis，并设置一个较短的过期时间
                            redisService.setMulti();
                            redisService.setCacheMapValue(orderHashKey, "","");
                            redisService.setExpireTime(orderHashKey,10, TimeUnit.SECONDS);
                            redisService.setExec();
                            objStr = redisService.getCacheMapValue(orderHashKey, orderNo);
                            orderInfo = JSONObject.parseObject(objStr, OrderInfo.class);
                        }else { //将数据放入redis中
                            redisService.setCacheMapValue(orderHashKey,orderInfo.getOrderNo(), JSON.toJSONString(orderInfo));
                        }
                    }
                }
                Thread.yield();
            }
        }
        return orderInfo;
    }

    @Override
    public String alipay(String orderNo) {
        OrderInfo orderInfo = this.selectOrderById(orderNo);
        AlipayVo vo = new AlipayVo();
        vo.setOutTradeNo(orderNo);
        vo.setSubject(orderInfo.getProductName());
        vo.setTotalAmount(String.valueOf(orderInfo.getSeckillPrice()));
        vo.setBody(orderInfo.getProductName());
        vo.setReturnUrl(returlUrl);
        vo.setNotifyUrl(notifyUrl);
        R<String> result = remoteAlipayService.pay(vo);
        if (StringUtils.isNull(result) || StringUtils.isNull(result.getData())) {
            throw new SeckillException(SeckillCodeMsg.PAY_ERROR);
        }
        return result.getData();
    }


    @Override
    public String alirefund(String orderNo) {
        OrderInfo orderInfo = this.selectOrderById(orderNo);
        RefundVo vo = new RefundVo();
        vo.setOutTradeNo(orderNo);
        vo.setRefundAmount(orderInfo.getSeckillPrice().toString());
        vo.setRefundReason("买多了");
        R<String> result = remoteAlipayService.refund(vo);
        if (StringUtils.isNull(result) || StringUtils.isNull(result.getData())) {
            throw new SeckillException(SeckillCodeMsg.REFUND_ERROR);
        }
        return result.getData();
    }

    @Override
    @Transactional
    public void payDone(String orderNo) {
        OrderInfo orderInfo = this.selectOrderById(orderNo);
        //插入支付日志
        PayLogVo log = new PayLogVo();
        log.setOrderNo(orderNo);
        log.setPayTime(new Date());
        log.setTotalAmount(orderInfo.getSeckillPrice().longValue());
        log.setPayType(OrderInfo.PAYTYPE_ONLINE);
        payLogMapper.insert(log);
        //更新订单状态
        int count = orderInfoMapper.updatePayStatus(orderNo, OrderInfo.STATUS_ACCOUNT_PAID, orderInfo.getPayType());
        if (count == 0) {
            //记录日志
            throw new SeckillException(SeckillCodeMsg.PAY_ERROR);
        }
    }

    @Override
    @GlobalTransactional
    public void intergralPay(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.find(orderNo);
        //1.插入日志,保证幂等性
        PayLogVo log = new PayLogVo();
        log.setOrderNo(orderNo);
        log.setPayTime(new Date());
        log.setTotalAmount(orderInfo.getIntergral());
        log.setPayType(PayLogVo.PAY_TYPE_INTERGRAL);

        payLogMapper.insert(log);
        //2.调用远程积分接口
        OperateIntergralVo vo = new OperateIntergralVo();
        vo.setUserId(orderInfo.getUserId());
        vo.setValue(orderInfo.getIntergral());
        R<String> result = remoteIntergralService.decrIntergral(vo);
        if (StringUtils.isNull(result) || StringUtils.isNull(result.getData())) {
            throw new SeckillException(SeckillCodeMsg.INTERGRAL_SERVER_ERROR);
        }
        //3.更新订单状态
        int count = orderInfoMapper.updatePayStatus(orderNo, OrderInfo.STATUS_ACCOUNT_PAID, OrderInfo.PAYTYPE_INTERGRAL);
        if (count == 0) {
            throw new SeckillException(SeckillCodeMsg.PAY_ERROR);
        }

//        int i = 1/0;
    }

    @Override
    @Transactional
    public int cancelOrder(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.find(orderNo);
        if (OrderInfo.STATUS_ARREARAGE.equals(orderInfo.getStatus())) {
            //修改订单状态-运用状态机模式避免多线程修改订单状态出错（乐观锁思想）
            int resCount = orderInfoMapper.updateCancelStatus(orderNo, OrderInfo.STATUS_CANCEL);
            if (resCount == 0) { //如果为0，说明已经被其他线程修改状态了
                return -1;
            }
            //增加真实库存
            seckillProductService.incrStockCount(orderInfo.getSeckillId());
            //同步预库存
            seckillProductService.syncRedisStock(orderInfo.getSeckillTime(), orderInfo.getSeckillId());

            //删除存储在redis的set集合中存放下单成功后的用户id
            Long seckillId = seckillProductMapper.findSeckillId(orderInfo.getProductId());
            String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(seckillId));
            redisService.deleteCacheSet(orderSetKey, orderInfo.getUserId().toString());
            System.out.println("取消订单成功");
        }
        return 0;
    }
}
