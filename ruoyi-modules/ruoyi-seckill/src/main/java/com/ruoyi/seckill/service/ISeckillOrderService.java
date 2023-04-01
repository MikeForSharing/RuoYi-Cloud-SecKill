package com.ruoyi.seckill.service;

import com.ruoyi.seckill.api.model.SeckillProductVo;

/**
 * @Author: zhangJiang
 */
public interface ISeckillOrderService {
    String createOrderInfo(String userId, SeckillProductVo vo);

    int cancelOrder(String orderNo);

    String doSeckill(String userId, SeckillProductVo seckillProductVo);

}
