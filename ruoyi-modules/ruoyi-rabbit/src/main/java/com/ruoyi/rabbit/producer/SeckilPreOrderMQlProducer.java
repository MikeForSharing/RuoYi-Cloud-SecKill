package com.ruoyi.rabbit.producer;

import com.ruoyi.common.core.constant.RabbitConstants;
import com.ruoyi.common.core.utils.uuid.IdUtils;
import com.ruoyi.seckill.api.model.OrderMQResult;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

import static com.ruoyi.rabbit.config.RabbitMQConfiguration.*;

/**
 * @Author: zhangJiang
 */
@Component
public class SeckilPreOrderMQlProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendPreOrder(String message) {
        String uniqueId = IdUtils.fastUUID();
        MessageProperties properties = new MessageProperties();
        properties.setContentEncoding("utf-8");
        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        Message messageFormat = new Message(message.getBytes(), properties);
        rabbitTemplate.convertAndSend(SECKILL_PRE_ORDER_QUEUE_NAME, SECKILL_PRE_ORDER_QUEUE_ROUTING_KEY, messageFormat,new CorrelationData(uniqueId));
    }

    public void sendOrderResult(String message, String flag) {
        if (flag.equals(RabbitConstants.ORDER_RESULT_SUCCESS_TAG)){ //进入秒杀队列成功
            String uniqueId = IdUtils.fastUUID();
            MessageProperties properties = new MessageProperties();
            properties.setContentEncoding("utf-8");
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            Message messageFormat = new Message(message.getBytes(), properties);
            rabbitTemplate.convertAndSend(SECKILL_PRE_ORDER_DELAY_QUEUE_NAME, SECKILL_PRE_ORDER_DELAY_QUEUE_ROUTING_KEY, messageFormat,new CorrelationData(uniqueId));
        }else if (flag.equals(RabbitConstants.ORDER_RESULT_FAIL_TAG)){ //购买秒杀商品失败（在创建订单的过程中出现异常后被捕获的情况）
            //将失败信息放入消息队列，进而将真实库存数量更新至redis中，以便放入更多请求继续购买商品
            String uniqueId = IdUtils.fastUUID();
            MessageProperties properties = new MessageProperties();
            properties.setContentEncoding("utf-8");
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            Message messageFormat = new Message(message.getBytes(), properties);
            rabbitTemplate.convertAndSend(SECKILL_ORDER_RES_FAIL_QUEUE_NAME, SECKILL_ORDER_RES_FAIL_QUEUE_ROUTING_KEY, messageFormat,new CorrelationData(uniqueId));
        }

    }
}
