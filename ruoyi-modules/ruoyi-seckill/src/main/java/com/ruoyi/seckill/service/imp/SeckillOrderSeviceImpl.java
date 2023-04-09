package com.ruoyi.seckill.service.imp;

import com.ruoyi.common.core.exception.seckill.SeckillException;
import com.ruoyi.common.core.utils.uuid.IdUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.seckill.api.model.OrderInfo;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import com.ruoyi.seckill.enums.SeckillRedisKey;
import com.ruoyi.seckill.mapper.OrderInfoMapper;
import com.ruoyi.seckill.mapper.SeckillProductMapper;
import com.ruoyi.seckill.service.ISeckillOrderService;
import com.ruoyi.seckill.service.ISeckillProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
        String key = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(seckillProductVo.getId()));
        redisService.addCacheSet(key, userId);
        return info.getOrderNo();
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
            System.out.println("取消订单成功" );
        }
        return 0;
    }

    @Override
    @Transactional
    public String doSeckill(String userId, SeckillProductVo vo) {
        int count = seckillProductMapper.decrStock(vo.getId());
        if (count == 0) {
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
}
