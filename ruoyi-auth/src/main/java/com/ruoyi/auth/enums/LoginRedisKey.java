package com.ruoyi.auth.enums;


import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Mike
 */

@Getter
public enum LoginRedisKey {
    USERLOGIN_HASH("userLoginHash"), USERINFO_HASH("userInfoHash"),USER_ZSET("userZset");
    LoginRedisKey(String prefix){
        this .prefix = prefix;
    }
    LoginRedisKey(String prefix, TimeUnit unit, int expireTime){
        this.prefix = prefix;
        this.unit = unit;
        this.expireTime = expireTime;
    }
    public String getRealKey(String key){
        return this.prefix+key;
    }
    private String prefix;
    private TimeUnit unit;
    private int expireTime;
}
