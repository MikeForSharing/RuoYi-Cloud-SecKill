package com.ruoyi.seckill.service;

import com.ruoyi.seckill.api.model.OrderInfo;
import com.ruoyi.seckill.api.model.SeckillProductVo;

/**
 * @Author: zhangJiang
 */
public interface ISeckillOrderService {
    String createOrderInfo(String userId, SeckillProductVo vo);

    int cancelOrder(String orderNo);

    String doSeckill(String userId, SeckillProductVo seckillProductVo);

    OrderInfo selectOrderById(String orderNo);

    String alipay(String orderNo);

    void payDone(String orderNo);

    void intergralPay(String orderNo);

    String alirefund(String orderNo);
}
