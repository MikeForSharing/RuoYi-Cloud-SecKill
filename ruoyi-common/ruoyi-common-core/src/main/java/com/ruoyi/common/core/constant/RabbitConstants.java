package com.ruoyi.common.core.constant;

/**
 * 消息中间件常量
 * @Author: mike
 * @Date: 2022/10/28
 * @Time： 10:49
 * @Version： 1.0.0
 */
public class RabbitConstants {

    /**
     * 消息类型为激活邮件
     */
    public static final Integer EMAIL_CODE_TYPE = 0;


    /**
     * redis中messageId过期时间
     */
    public static final Long REDIS_MESSAGEID_EXPIRATION = Long.valueOf(10);



    /**
     * redis中message的消费状态 0:未消费 1:已消费
     */
    public static final String REDIS_MESSAGE_CONSUME_NO = "0";
    public static final String REDIS_MESSAGE_CONSUME_YES = "1";



    /**
     * 消息延迟时间的类型 1:30m  2:60m
     */
    public static final Integer MESSAGE_DELAYTIME_10 = 1;
    public static final Integer MESSAGE_DELAYTIME_60 = 2;

}
