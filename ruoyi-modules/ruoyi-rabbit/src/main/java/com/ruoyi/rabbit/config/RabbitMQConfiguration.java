package com.ruoyi.rabbit.config;

import com.rabbitmq.client.ConfirmCallback;
//import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Return;
import com.rabbitmq.client.ReturnCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class RabbitMQConfiguration implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

//    @Bean("delayExchange")
//    public ConnectionFactory connectionFactory() {
//        return new ConnectionFactory();
//    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback(this);

        //开启了回退模式并设置了Mandatory，当消息从exchange发送到queue失败了，则会在消息回退到producer,并执行回调函数
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(this);


        return rabbitTemplate;

    }

//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        factory.setConnectionFactory(connectionFactory);
////        factory.setMessageConverter(new Jackson2JsonMessageConverter());
//        return factory;
//    }

    /**
     * 交换机收到生产者发送消息后的回调函数
     * @param correlationData  给消息设置相关消息和全局消息id
     * @param ack
     * @param reason  交换机未收到消息的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String reason) {

        String id = correlationData != null ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机收到消息，消息的id为："+ id);
        }else {
            log.info("交换机未收到消息，消息的id为："+ id + "原因是："+ reason);
        }

    }

    /**
     * 队列未收到交换机路由的消息时触发该函数
     * @param message  消息的内容
     * @param replyCode
     * @param replyContext  消息被退回的原因
     * @param exchange  交换机
     * @param routeKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyContext, String exchange, String routeKey) {
        log.error("交换机{}退回了消息{}，原因是{}，路由是{}",new String(message.getBody()),exchange,replyContext,routeKey);

//        spring_returned_message_correlation：所退回消息的唯一标识，可用于更新自定义的消息记录表
        log.info("消息的唯一标识："+message.getMessageProperties().getHeader("spring_returned_message_correlation").toString());
    }


    // 声明 4个路由 key 4个队列 2个交换机
    // 延迟交换机
    public static final String DELAY_EXCHANGE_NAME = "delay.exchange";
    // 延迟队列
    public static final String DELAY_QUEUE_A_NAME = "delay.queue.a";
    public static final String DELAY_QUEUE_B_NAME = "delay.queue.b";
    // 延迟队列路由 key
    public static final String DELAY_QUEUE_A_ROUTING_KEY = "delay.queue.a.routingkey";
    public static final String DELAY_QUEUE_B_ROUTING_KEY = "delay.queue.b.routingkey";
    // 死信交换机
    public static final String DEAD_LETTER_EXCHANGE_NAME = "dead.letter.exchange";
    // 死信队列
    public static final String DEAD_LETTER_QUEUE_A_NAME = "dead.letter.queue.a";
    public static final String DEAD_LETTER_QUEUE_B_NAME = "dead.letter.queue.b";
    // 死信队列路由 key
    public static final String DEAD_LETTER_QUEUE_A_ROUTING_KEY = "dead.letter.delay_30m.routingkey";
    public static final String DEAD_LETTER_QUEUE_B_ROUTING_KEY = "dead.letter.delay_60m.routingkey";

    // 声明延迟交换机，默认是持久化
    @Bean("delayExchange")
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE_NAME);
    }

    // 声明死信交换机
    @Bean("deadLetterExchange")
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE_NAME);
    }

    // 声明延迟队列A，延迟30m，并且绑定到对应的死信交换机
    @Bean("delayQueueA")
    public Queue delayQueueA() {
        Map<String, Object> args = new HashMap<>();
        // x-dead-letter-exchange 声明队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE_NAME);
        // x-dead-letter-routing-key 声明队列的死信路由Key
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUE_A_ROUTING_KEY);
        // 声明队列的消息 TTL 存活时间
        args.put("x-message-ttl", 1800000);
        return QueueBuilder.durable(DELAY_QUEUE_A_NAME).withArguments(args).build();
    }

    // 声明延迟队列B，延迟60m，并且绑定到对应的死信交换机
    @Bean("delayQueueB")
    public Queue delayQueueB() {
        Map<String, Object> args = new HashMap<>();
        // x-dead-letter-exchange 声明队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE_NAME);
        // x-dead-letter-routing-key 声明队列的死信路由Key
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUE_B_ROUTING_KEY);
        // 声明队列的消息 TTL 存活时间
        args.put("x-message-ttl", 3600000);
        return QueueBuilder.durable(DELAY_QUEUE_B_NAME).withArguments(args).build();
    }

    // 声明延迟队列A的绑定关系
    @Bean
    public Binding delayBindingA(@Qualifier("delayQueueA") Queue queue,
                                 @Qualifier("delayExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DELAY_QUEUE_A_ROUTING_KEY);
    }

    // 声明延迟队列B的绑定关系
    @Bean
    public Binding delayBindingB(@Qualifier("delayQueueB") Queue queue,
                                 @Qualifier("delayExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DELAY_QUEUE_B_ROUTING_KEY);
    }

    // 声明死信队列A 用于接收延迟10s处理的消息
    @Bean("deadLetterQueueA")
    public Queue deadLetterQueueA() {
        return new Queue(DEAD_LETTER_QUEUE_A_NAME);
    }

    // 声明死信队列A 用于接收延迟60m处理的消息
    @Bean("deadLetterQueueB")
    public Queue deadLetterQueueB() {
        return new Queue(DEAD_LETTER_QUEUE_B_NAME);
    }

    // 声明死信队列A的绑定关系
    @Bean
    public Binding deadLetterBindingA(@Qualifier("deadLetterQueueA") Queue queue,
                                      @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEAD_LETTER_QUEUE_A_ROUTING_KEY);
    }

    // 声明死信队列B的绑定关系
    @Bean
    public Binding deadLetterBindingB(@Qualifier("deadLetterQueueB") Queue queue,
                                      @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEAD_LETTER_QUEUE_B_ROUTING_KEY);
    }



}
