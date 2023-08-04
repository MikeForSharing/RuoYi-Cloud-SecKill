package com.ruoyi.product.api.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Author: Mike
 */
@Setter
@Getter
public class Product {
    private Long id;//商品iD
    private String productName;//商品名称
    private String productTitle;//商品标题
    private String productImg;//商品图片
    private String productDetail;//商品明细
    private BigDecimal productPrice;//商品价格
}
