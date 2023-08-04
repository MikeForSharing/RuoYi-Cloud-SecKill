package com.ruoyi.seckill.mq;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.ruoyi.common.core.constant.RabbitConstants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.seckill.api.RemoteSeckillOrderService;
import com.ruoyi.seckill.api.RemoteSeckillProductService;
import com.ruoyi.seckill.api.model.OrderMQResult;
import com.ruoyi.seckill.service.ISeckillProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
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
public class SeckillOrderResFailConsumer {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RemoteSeckillProductService remoteSeckillProductService;

    @Autowired
    private RemoteSeckillOrderService remoteSeckillOrderService;

    @Autowired
    private ISeckillProductService seckillProductService;


//    // 监听秒杀失败结果（在创建订单失败、在创建订单的过程中出现异常后被捕获的情况下执行，创建订单成功，就进入延迟队列了）
//    @RabbitListener(queues = SECKILL_ORDER_RES_QUEUE_NAME)
//    public void receiveNormal(Message message, Channel channel) throws Exception {
//        System.out.println("222");
//        this.messageHan(message, channel);
//    }
//    @RabbitListener(queues = SECKILL_ORDER_RES_TOPIC_QUEUE_NAME)
//    public void receiveNormal(Message message, Channel channel) throws Exception {
//        System.out.println("222");
//        this.messageHan(message, channel);
//    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(), //注意这里不要定义队列名称,系统会随机产生
            exchange = @Exchange(value = SECKILL_ORDER_RES_FANOUT_EXCHANGE_NAME,type = ExchangeTypes.FANOUT)
    ))
    public void receiveNormal(Message message, Channel channel) throws Exception {
        this.messageHan(message, channel);
    }


    //处理秒杀失败结果消息
    public void messageHan(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = message.getMessageProperties().getHeader("spring_returned_message_correlation");

        //测试用-临时消费多余消息 FIXME
//        channel.basicAck(deliveryTag, false);

        try {
            //消费者在消费消息之前，先去redis中查看消息状态是否已被消费，0：未消费  1：已消费
            //此处取消校验“消息重复消费”功能，因为websocket模块和seckill模块都监听SECKILL_ORDER_RES_FANOUT_EXCHANGE_NAME交换机
//            if (redisService.setCacheObjectIfAbsent(messageId, RabbitConstants.REDIS_MESSAGE_CONSUME_NO, RabbitConstants.REDIS_MESSAGEID_EXPIRATION, TimeUnit.SECONDS)) {
                log.info("收到消息：" + new String(message.getBody()));
                // 获取消息
                String msg = new String(message.getBody());
                OrderMQResult omqMsg = JSONObject.parseObject(msg, OrderMQResult.class);
                if (RabbitConstants.ORDER_RESULT_FAIL_TAG.equals(omqMsg.getOrderRes())){
                    //将真实库存数量更新至redis中，以便放入更多请求继续购买商品
//                remoteSeckillProductService.syncRedisStock(omqMsg.getTime(),omqMsg.getSeckillId(), SecurityConstants.INNER);
                    seckillProductService.syncRedisStock(omqMsg.getTime(), omqMsg.getSeckillId());
                    //消费完消息后，设置key的值为1，表示已消费
                    redisService.setCacheObject(messageId, RabbitConstants.REDIS_MESSAGE_CONSUME_YES, RabbitConstants.REDIS_MESSAGEID_EXPIRATION, TimeUnit.SECONDS);
                }
                //手动确认消息
                channel.basicAck(deliveryTag, false);
//            } else {
//                //如果从redis中获取消息的value是1，表示已消费，直接发送确认信号，避免重复消费
//                if ("1".equals(redisService.getCacheObject(messageId))) {
//                    //手动确认消息
//                    channel.basicAck(deliveryTag, false);
//                }
//            }
            log.info("当前时间：{}，seckill队列收到消息：{}", LocalDateTime.now(), new String(message.getBody()));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("再次将消息退回队列");
            channel.basicNack(deliveryTag, true, true);
        }
    }


}
