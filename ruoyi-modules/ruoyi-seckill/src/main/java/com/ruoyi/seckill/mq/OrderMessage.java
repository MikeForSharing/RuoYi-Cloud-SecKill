package com.ruoyi.seckill.mq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by wolfcode-lanxw
 * 封装异步下单的参数
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage implements Serializable {
    private Integer time;//秒杀场次
    private Long seckillId;//秒杀商品ID
    private String token;//用户的token信息
    private Long userId;//用户手机号码
}
