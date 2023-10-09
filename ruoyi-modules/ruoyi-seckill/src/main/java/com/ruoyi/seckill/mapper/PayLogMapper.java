package com.ruoyi.seckill.mapper;

import com.ruoyi.seckill.api.model.PayLogVo;

/**
 * @Author: zhangJiang
 */
public interface PayLogMapper {
    /**
     * 插入支付日志，用于幂等性控制
     * @param payLog
     * @return
     */
    int insert(PayLogVo payLog);
}
