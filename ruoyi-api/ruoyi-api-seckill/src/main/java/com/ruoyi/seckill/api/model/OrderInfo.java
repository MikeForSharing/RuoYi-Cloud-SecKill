package com.ruoyi.seckill.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: zhangJiang
 */
@Setter
@Getter
public class OrderInfo implements Serializable {
    public static final Integer STATUS_ARREARAGE = 0;//未付款
    public static final Integer STATUS_ACCOUNT_PAID = 1;//已付款
    public static final Integer STATUS_CANCEL = 2;//手动取消订单
    public static final Integer STATUS_TIMEOUT = 3;//超时取消订单
    public static final Integer STATUS_REFUND = 4;//已退款
    public static final Integer PAYTYPE_ONLINE = 0;//在线支付
    public static final Integer PAYTYPE_INTERGRAL = 1;//积分支付
    private String orderNo;//订单编号
    private Long userId;//用户ID
    private Long productId;//商品ID
    private Long deliveryAddrId;//收货地址
    private String productName;//商品名称
    private String productImg;//商品图片
    private BigDecimal productPrice;//商品原价
    private BigDecimal seckillPrice;//秒杀价格
    private Long intergral;//消耗积分
    private Integer status = STATUS_ARREARAGE;//订单状态
    private Date createDate;//订单创建时间
    private Date payDate;//订单支付时间
    private int payType;//支付方式 1-在线支付 2-积分支付
    private Date seckillDate;//秒杀的日期
    private Integer seckillTime;// 秒杀场次
    private Long seckillId;//秒杀商品ID
}