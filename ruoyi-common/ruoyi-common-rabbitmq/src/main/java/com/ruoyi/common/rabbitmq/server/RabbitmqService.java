package com.ruoyi.common.rabbitmq.server;

import com.ruoyi.common.core.utils.uuid.IdUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: zhangJiang
 * @Version： 1.0.0
 */
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Service
public class RabbitmqService {

    @Resource
    private RabbitTemplate rabbitTemplate;


    public void rabbitSend(String echangeName,String routeKey,String message){
        String uniqueId = IdUtils.fastUUID();
        MessageProperties properties = new MessageProperties();
        properties.setContentEncoding("utf-8");
        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        Message messageFormat = new Message(message.getBytes(), properties);
        rabbitTemplate.convertAndSend(echangeName, routeKey, messageFormat,new CorrelationData(uniqueId));
    }


}
