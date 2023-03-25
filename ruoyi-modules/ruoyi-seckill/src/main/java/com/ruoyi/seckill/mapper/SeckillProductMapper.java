package com.ruoyi.seckill.mapper;

import com.ruoyi.seckill.api.model.SeckillProduct;

import java.util.List;

/**
 * 秒杀产品 数据层
 *
 * @Author: Mike
 */
public interface SeckillProductMapper {
    /**
     * 根据秒杀场次查询秒杀商品列表
     * @param time
     * @return
     */
    List<SeckillProduct> queryCurrentlySeckillProduct(Integer time);
}
