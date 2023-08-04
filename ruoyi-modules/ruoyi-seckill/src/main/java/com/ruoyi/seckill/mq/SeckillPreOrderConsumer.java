package com.ruoyi.seckill.mq;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.ruoyi.common.core.constant.RabbitConstants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.rabbitmq.server.RabbitmqService;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.seckill.api.RemoteSeckillOrderService;
import com.ruoyi.seckill.api.RemoteSeckillProductService;
import com.ruoyi.seckill.api.model.OrderMQResult;
import com.ruoyi.seckill.api.model.SeckillCodeMsg;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import com.ruoyi.seckill.service.ISeckillOrderService;
import com.ruoyi.seckill.service.ISeckillProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.ruoyi.common.rabbitmq.config.RabbitMQConfiguration.*;

/**
 * @Author: zhangJiang
 */
@Slf4j
@Component
public class SeckillPreOrderConsumer {

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

    // 监听订单支付普通队列-创建订单
    @RabbitListener(queues = SECKILL_PRE_ORDER_QUEUE_NAME)
    public void receiveNormal(Message message, Channel channel) throws Exception {
        this.messageHan(message, channel);
    }


    //处理正常消息
    public void messageHan(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = message.getMessageProperties().getHeader("spring_returned_message_correlation");

        //测试用-临时消费多余消息 todo
//        channel.basicAck(deliveryTag, false);

        try {
            //消费者在消费消息之前，先去redis中查看消息状态是否已被消费，0：未消费  1：已消费
            if (redisService.setCacheObjectIfAbsent(messageId, RabbitConstants.REDIS_MESSAGE_CONSUME_NO, RabbitConstants.REDIS_MESSAGEID_EXPIRATION, TimeUnit.SECONDS)) {
                log.info("收到消息：" + new String(message.getBody()));
                // 获取消息
                String msg = new String(message.getBody());
                OrderMessage orderMsg = JSONObject.parseObject(msg, OrderMessage.class);
                OrderMQResult result = new OrderMQResult();
                result.setTime(orderMsg.getTime());
                result.setSeckillId(orderMsg.getSeckillId());
//                String exchangeName = null;
//                String routeName = null;
                String flag = null;
                //监听死信队列的程序在到达规定时间段后会自动执行相应操作
                try {
                    // 业务处理
                    flag = RabbitConstants.ORDER_RESULT_SUCCESS_TAG;
                    SeckillProductVo resSeckillProductVo =seckillProductService.findFromCache(orderMsg.getTime(), orderMsg.getSeckillId());
                    String resOrderNo = seckillOrderService.doSeckill(String.valueOf(orderMsg.getUserId()), resSeckillProductVo);
                    result.setOrderNo(resOrderNo);
                    result.setOrderRes(flag);
//                    exchangeName = SECKILL_PRE_ORDER_DELAY_EXCHANGE_NAME;
//                    routeName = SECKILL_PRE_ORDER_DELAY_QUEUE_ROUTING_KEY;
                    rabbitmqService.rabbitSend(SECKILL_PRE_ORDER_DELAY_EXCHANGE_NAME, SECKILL_PRE_ORDER_DELAY_QUEUE_ROUTING_KEY, JSONObject.toJSONString(result));
                } catch (Exception e) {
                    //在创建订单的过程中出现异常后被捕获的情况
                    flag = RabbitConstants.ORDER_RESULT_FAIL_TAG;
                    e.printStackTrace();
                    result.setCode(SeckillCodeMsg.SECKILL_ERROR.getCode());
                    result.setMsg(SeckillCodeMsg.SECKILL_ERROR.getMsg());
                    result.setOrderRes(flag);
//                    exchangeName = SECKILL_ORDER_RES_EXCHANGE_NAME;
//                    routeName = SECKILL_ORDER_RES_QUEUE_ROUTING_KEY;
                }
                //FIXME
//                result.setToken(SecurityUtils.getToken());
                result.setToken("111");

                //无论秒杀操作成功与失败，都要发结果消息给消息中间件
//                seckillProducer.sendOrderResult(result.toString(),flag);
//                                rabbitmqService.rabbitSend(SECKILL_ORDER_RES_EXCHANGE_NAME, SECKILL_ORDER_RES_QUEUE_ROUTING_KEY, JSONObject.toJSONString(result));
//                rabbitmqService.rabbitSend(SECKILL_ORDER_RES_TOPIC_EXCHANGE_NAME, SECKILL_ORDER_RES_TOPIC_QUEUE_ROUTING_KEY, JSONObject.toJSONString(result));
                rabbitmqService.rabbitSend(SECKILL_ORDER_RES_FANOUT_EXCHANGE_NAME,"", JSONObject.toJSONString(result));



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
            log.info("当前时间：{}，seckill队列收到消息：{}", LocalDateTime.now(), new String(message.getBody()));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("再次将消息退回队列");
            channel.basicNack(deliveryTag, true, true);
        }
    }



}
