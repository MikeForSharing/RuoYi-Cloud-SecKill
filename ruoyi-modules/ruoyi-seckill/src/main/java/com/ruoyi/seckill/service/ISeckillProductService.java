package com.ruoyi.seckill.service;

import com.ruoyi.seckill.api.model.SeckillProductVo;

import java.util.List;

/**
 * @Author: Mike
 */
public interface ISeckillProductService {
    List<SeckillProductVo> querySeckillProductListByTime(Integer time);

    List<SeckillProductVo> querySeckillProductListByTimeFromCache(Integer time);

    SeckillProductVo findFromCache(Integer time, Long seckillId);


    void incrStockCount(Long seckillId);

    void syncRedisStock(Integer time, Long seckillId);

}
