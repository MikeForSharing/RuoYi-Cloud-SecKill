package com.ruoyi.seckill.mq;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.ruoyi.common.core.constant.RabbitConstants;
import com.ruoyi.common.rabbitmq.server.RabbitmqService;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.seckill.api.RemoteSeckillOrderService;
import com.ruoyi.seckill.api.RemoteSeckillProductService;
import com.ruoyi.seckill.api.model.OrderMQResult;
import com.ruoyi.seckill.service.ISeckillOrderService;
import com.ruoyi.seckill.service.ISeckillProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.ruoyi.common.rabbitmq.config.RabbitMQConfiguration.*;

/**
 * @Author: zhangJiang
 */
@Slf4j
@Component
public class SeckillPayDelayConsumer {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RemoteSeckillProductService remoteSeckillProductService;

    @Autowired
    private RemoteSeckillOrderService remoteSeckillOrderService;


    @Autowired
    private RabbitmqService rabbitmqService;


    @Autowired
    private ISeckillProductService seckillProductService;

    @Autowired
    private ISeckillOrderService seckillOrderService;


    // 监听订单支付延迟队列-若在规定时间内未支付，则取消订单
    @RabbitListener(queues = SECKILL_PRE_ORDER_DEAD_LETTER_QUEUE_NAME)
    public void receiveA(Message message, Channel channel) throws Exception {
        this.messageDelayHan(message, channel);
    }

    //处理延迟消息
    public void messageDelayHan(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = message.getMessageProperties().getHeader("spring_returned_message_correlation");
        try {
            //消费者在消费消息之前，先去redis中查看消息状态是否已被消费，0：未消费  1：已消费
            if (redisService.setCacheObjectIfAbsent(messageId, RabbitConstants.REDIS_MESSAGE_CONSUME_NO, RabbitConstants.REDIS_MESSAGEID_EXPIRATION, TimeUnit.SECONDS)) {
                log.info("收到延迟消息：" + new String(message.getBody()));
                // 获取消息
                String msg = new String(message.getBody());
                OrderMQResult omqMsg = JSONObject.parseObject(msg, OrderMQResult.class);

                //FIXME
                try {
                    seckillOrderService.cancelOrder(omqMsg.getOrderNo());
                }catch (Exception e){
                    e.printStackTrace();
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
            log.info("当前时间：{}，seckill队列收到延时消息：{}", LocalDateTime.now(), new String(message.getBody()));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("再次将消息退回队列");
            channel.basicNack(deliveryTag, true, true);
        }
    }

}
