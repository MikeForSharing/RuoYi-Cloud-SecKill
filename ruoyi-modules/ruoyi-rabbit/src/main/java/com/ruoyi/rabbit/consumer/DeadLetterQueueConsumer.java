package com.ruoyi.rabbit.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.ruoyi.common.core.constant.RabbitConstants;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.rabbit.service.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.ruoyi.rabbit.config.RabbitMQConfiguration.DEAD_LETTER_QUEUE_A_NAME;
import static com.ruoyi.rabbit.config.RabbitMQConfiguration.DEAD_LETTER_QUEUE_B_NAME;


@Slf4j
@Component
public class DeadLetterQueueConsumer {
    @Resource
    private RabbitService rabbitService;

    @Autowired
    private RedisService redisService;

    // 监听死信队列A
    @RabbitListener(queues = DEAD_LETTER_QUEUE_A_NAME)
    public void receiveA(Message message, Channel channel) throws Exception {
        this.messageHan(message, channel);
    }

    // 监听死信队列B
    @RabbitListener(queues = DEAD_LETTER_QUEUE_B_NAME)
    public void receiveB(Message message, Channel channel)  throws Exception{
        this.messageHan(message, channel);
    }

    //处理消息
    public void messageHan(Message message, Channel channel) throws Exception{
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = message.getMessageProperties().getHeader("spring_returned_message_correlation");
        try {
            //消费者在消费消息之前，先去redis中查看消息状态是否已被消费，0：未消费  1：已消费
            if (redisService.setCacheObjectIfAbsent(messageId, RabbitConstants.REDIS_MESSAGE_CONSUME_NO, RabbitConstants.REDIS_MESSAGEID_EXPIRATION, TimeUnit.SECONDS)) {
                log.info("收到消息：" + new String(message.getBody()));
                // 获取消息
                String msg = new String(message.getBody());
                JSONObject jsonObject = JSONObject.parseObject(msg);
                // 判断消息类型是否为邮件激活码激活帐户
                if (jsonObject.getInteger("type") == RabbitConstants.EMAIL_CODE_TYPE) {
                    String emailCode = JSONObject.parseObject(jsonObject.getString("activeCode")).get("data").toString();
                    if (!rabbitService.isActive(emailCode)) {  //邮箱在有效期内未激活
                        Long userId = Long.valueOf(jsonObject.getString("userId"));
                        rabbitService.deleteUserById(userId);
                    }
                }
                //消费完消息后，设置key的值为1，表示已消费
                redisService.setCacheObject(messageId, RabbitConstants.REDIS_MESSAGE_CONSUME_YES, RabbitConstants.REDIS_MESSAGEID_EXPIRATION, TimeUnit.SECONDS);
                //手动确认消息
                channel.basicAck(deliveryTag, false);
            } else {
                //如果从redis中获取消息的value是1，表示已消费，直接发送确认信号，避免重复消费
                if ("1".equals(redisService.getCacheObject(messageId))) {
                    //手动确认消息
                    channel.basicAck(deliveryTag, false);
                }
            }
            log.info("当前时间：{}，死信队列A收到消息：{}", LocalDateTime.now(), new String(message.getBody()));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("再次将消息退回队列");
            channel.basicNack(deliveryTag, true, true);
        }
    }

}
