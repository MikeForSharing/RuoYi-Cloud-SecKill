package com.ruoyi.seckill.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 表现层对象-整合Seckillproduct和Product对象
 *
 * @author Mike
 */
@Setter
@Getter
public class SeckillProductVo extends SeckillProduct implements Serializable {
    private String productName;
    private String productTitle;
    private String productImg;
    private String productDetail;
    private BigDecimal productPrice;
    private Integer currentCount;
}
