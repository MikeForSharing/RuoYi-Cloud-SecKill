package com.ruoyi.canal.processer;


import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.redis.enums.SeckillRedisKey;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.seckill.api.model.OrderInfo;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

import java.util.concurrent.TimeUnit;

/**
 * 订单信息
 *
 * @Author: Mike
 */
@CanalTable(value = "t_order_info")
@Component
@Slf4j
public class OrderInfoProcessor implements EntryHandler<OrderInfo> {

    @Autowired
    private RedisService redisService;

    /**
     *  新增操作
     * @param orderInfo
     */
    @Override
    public void insert(OrderInfo orderInfo) {
        log.info("新增 {}",orderInfo);
        //在redis的set集合中存放下单成功后的用户id，用于redis中查询是否重复下单
        //seckillOrderSet:12 [userId1,userId2,...]  ，或者通过canel可以实现，往数据库插入时自动同步到redis
        String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(orderInfo.getSeckillId()));
        redisService.addCacheSet(orderSetKey, orderInfo.getUserId().toString());

        //创建好的订单对象，存储到Redis的hash结构中
        String orderHashKey = SeckillRedisKey.SECKILL_ORDER_HASH.getRealKey("");
        redisService.setCacheMapValue(orderHashKey,orderInfo.getOrderNo(),JSON.toJSONString(orderInfo));
    }
    /**
     * 对于更新操作来讲，before 中的属性只包含变更的属性，after 包含所有属性，通过对比可发现那些属性更新了
     * @param before
     * @param after
     */
    @Override
    public void update(OrderInfo before, OrderInfo after) {
        log.info("更新 {} {}",before,after);
        String orderHashKey = SeckillRedisKey.SECKILL_ORDER_HASH.getRealKey("");
        //Redis 事务保持原子性   
        redisService.setMulti();
        redisService.setCacheMapValue(orderHashKey,after.getOrderNo(),JSON.toJSONString(after));
        redisService.setExpireTime(orderHashKey,6000*3, TimeUnit.SECONDS);
        redisService.setExec();
    }
    /**
     *  删除操作
     * @param orderInfo
     */
    @Override
    public void delete(OrderInfo orderInfo) {
        log.info("删除 {}",orderInfo);
    }
}
