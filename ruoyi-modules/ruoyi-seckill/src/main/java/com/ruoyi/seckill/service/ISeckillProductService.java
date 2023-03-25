package com.ruoyi.seckill.service;

import com.ruoyi.seckill.api.model.SeckillProductVo;

import java.util.List;

/**
 * @Author: Mike
 */
public interface ISeckillProductService {
    List<SeckillProductVo> querySeckillProductListByTime(Integer time);

    List<SeckillProductVo> querySeckillProductListByTimeFromCache(Integer time);


    SeckillProductVo find(String time, Long seckillId);
}
