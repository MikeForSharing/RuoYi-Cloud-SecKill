package com.ruoyi.elasticJob.enums;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Mike
 */
@Getter
public enum JobRedisKey {
    SECKILL_PRODUCT_HASH("seckillProductHash:" ),   //秒杀产品列表key
    SECKILL_STOCK_COUNT_HASH("seckillStockCount:" ),
    USERLOGIN_HASH("userLoginHash" ),
    USERINFO_HASH("userInfoHash" ),
    USER_ZSET("userZset" );

    JobRedisKey(String prefix, TimeUnit unit, int expireTime) {
        this.prefix = prefix;
        this.unit = unit;
        this.expireTime = expireTime;
    }

    JobRedisKey(String prefix) {
        this.prefix = prefix;
    }

    public String getRealKey(String key) {
        return this.prefix + key;
    }

    private String prefix;
    private TimeUnit unit;
    private int expireTime;
}
