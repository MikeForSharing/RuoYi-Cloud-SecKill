package com.ruoyi.rabbit.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: zhangJiang
 */
@Setter
@Getter
public class OrderMQResult implements Serializable {
    private Integer time;//秒杀场次
    private Long seckillId;//秒杀商品id
    private String orderNo;//订单编号
    private String msg;//提示消息
    private Integer code;//状态码
    private String token;//用户token
    private Boolean flag;//订单结果 true表示成功  false表示失败
}

