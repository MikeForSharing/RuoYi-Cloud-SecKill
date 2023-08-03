package com.ruoyi.rabbit.producer;

import com.ruoyi.common.core.utils.uuid.UUID;
import com.ruoyi.rabbit.enums.DelayTypeEnum;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.ruoyi.rabbit.config.RabbitMQConfiguration.*;


@Component
public class DelayMessageProducer20221121bak {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void send(String message, DelayTypeEnum type) {

        //另一种构建方式
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("email", "644064779");
//        jsonObject.put("timestamp", System.currentTimeMillis());
//        String jsonString = jsonObject.toJSONString();
//        System.out.println("jsonString:" + jsonString);
//        // 生产者发送消息的时候需要设置消息id
//        Message message = MessageBuilder.withBody(jsonString.getBytes())
//                .setContentType(MessageProperties.CONTENT_TYPE_JSON).setContentEncoding("utf-8")
//                .setMessageId(UUID.randomUUID() + "").build();

        String uniqueId = UUID.randomUUID().toString();
        MessageProperties properties = new MessageProperties();
//        properties.setMessageId(uniqueId);
//        properties.setContentType("text/plain");
        properties.setContentEncoding("utf-8" );
        //如果延迟队列没设置 x-message-ttl，消息存活时间为properties.setExpiration("5000");如果延迟队列设置了 x-message-ttl 消息存活时间，properties.setExpiration("5000")也设置了有效期，则取两者中较小值有效
//        properties.setExpiration("30000");
        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        Message messageHan = new Message(message.getBytes(), properties);

        switch (type) {
            case DELAY_30m:
                rabbitTemplate.convertAndSend(DELAY_EXCHANGE_NAME, DELAY_QUEUE_A_ROUTING_KEY, messageHan, new CorrelationData(uniqueId));
//                rabbitTemplate.convertAndSend(DELAY_EXCHANGE_NAME, DELAY_QUEUE_A_ROUTING_KEY, message);

                break;
            case DELAY_60m:
                rabbitTemplate.convertAndSend(DELAY_EXCHANGE_NAME, DELAY_QUEUE_B_ROUTING_KEY, message);
//                Map user = new HashMap<>();
//                rabbitTemplate.convertAndSend("userRegistQueue", "user.register", user);
                break;
            default:
        }
    }

}
