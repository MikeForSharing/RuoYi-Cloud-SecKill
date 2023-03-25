package com.ruoyi.seckill.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by lanxw
 * 秒杀商品表
 */
@Setter
@Getter
public class SeckillProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;//秒杀商品ID
    private Long productId;//商品ID
    private BigDecimal seckillPrice;//秒杀价格
    private Long intergral;//积分
    private Integer stockCount;//库存总数
    private Date startDate;//秒杀日期
    private Integer time;//秒杀场次
}
