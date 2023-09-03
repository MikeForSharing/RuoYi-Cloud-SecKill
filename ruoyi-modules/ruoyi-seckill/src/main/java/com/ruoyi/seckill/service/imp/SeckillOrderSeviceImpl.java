package com.ruoyi.seckill.service.imp;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.seckill.SeckillException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.uuid.IdUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.seckill.api.RemoteSeckillAlipayService;
import com.ruoyi.seckill.api.model.*;
import com.ruoyi.seckill.enums.SeckillRedisKey;
import com.ruoyi.seckill.mapper.OrderInfoMapper;
import com.ruoyi.seckill.mapper.SeckillProductMapper;
import com.ruoyi.seckill.service.ISeckillOrderService;
import com.ruoyi.seckill.service.ISeckillProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    private RemoteSeckillAlipayService remoteSeckillAlipayService;

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

        //
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
            throw new SeckillException("商品已卖完！" );
        }
        //使用数据库唯一索引保证用户不会重复下单
        String orderNo = createOrderInfo(userId, vo);

        //在redis的set集合中存放下单成功后的用户id，用于redis中查询是否重复下单
        //seckillOrderSet:12 [userId1,userId2,...]  ，或者通过canel可以实现，往数据库插入时自动同步到redis
        String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(vo.getId()));
        redisService.addCacheSet(orderSetKey, userId);
        return orderNo;
    }

    @Override
    public OrderInfo selectOrderById(String orderNo) {
        return orderInfoMapper.find(orderNo);
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
        R<String> result = remoteSeckillAlipayService.pay(vo);
        if (StringUtils.isNull(result) || StringUtils.isNull(result.getData())) {
            return "支付宝支付失败！";
        }
        return result.getData();
    }

    @Override
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
        int count = orderInfoMapper.changePayStatus(orderNo, OrderInfo.STATUS_ACCOUNT_PAID, orderInfo.getPayType());
        if(count==0){
            //记录日志
            throw new BusinessException(SeckillCodeMsg.PAY_ERROR);
        }
    }

    @Override
    @Transactional
    public int cancelOrder(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.find(orderNo);
        if (OrderInfo.STATUS_ARREARAGE.equals(orderInfo.getStatus())) {
            //修改订单状态-运用状态机模式避免多线程修改订单状态出错（乐观锁思想）
            int resCount = orderInfoMapper.updateCancelStatus(orderNo, OrderInfo.STATUS_CANCEL);
            if (resCount==0){ //如果为0，说明已经被其他线程修改状态了
                return -1;
            }
            //增加真实库存
            seckillProductService.incrStockCount(orderInfo.getSeckillId());
            //同步预库存
            seckillProductService.syncRedisStock(orderInfo.getSeckillTime(), orderInfo.getSeckillId());

            //删除存储在redis的set集合中存放下单成功后的用户id
            Long seckillId =  seckillProductMapper.findSeckillId(orderInfo.getProductId());
            String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(seckillId));
            redisService.deleteCacheSet(orderSetKey, orderInfo.getUserId().toString());
            System.out.println("取消订单成功" );
        }
        return 0;
    }
}
