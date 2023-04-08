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
     *
     * @param time
     * @return
     */
    List<SeckillProduct> queryCurrentlySeckillProduct(Integer time);


    /**
     * 对秒杀商品库存进行递减操作
     *
     * @param seckillId
     * @return
     */
    int decrStock(Long seckillId);

    /**
     * 对秒杀商品库存进行增加操作
     *
     * @param seckillId
     * @return
     */
    int incrStock(Long seckillId);


    /**
     * 获取数据库中商品库存的数量
     *
     * @param seckillId
     * @return
     */
    int getStockCount(Long seckillId);
}
