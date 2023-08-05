package com.ruoyi.common.rabbitmq.config;

//import com.rabbitmq.client.ConnectionFactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class RabbitMQConfiguration implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
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
     *
     * @param correlationData 给消息设置相关消息和全局消息id
     * @param ack
     * @param reason          交换机未收到消息的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String reason) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机收到消息，消息的id为：" + id);
        } else {
            log.info("交换机未收到消息，消息的id为：" + id + "原因是：" + reason);
        }
    }

    /**
     * 队列未收到交换机路由的消息时触发该函数
     *
     * @param message      消息的内容
     * @param replyCode
     * @param replyContext 消息被退回的原因
     * @param exchange     交换机
     * @param routeKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyContext, String exchange, String routeKey) {
        log.error("交换机{}退回了消息{}，原因是{}，路由是{}" , new String(message.getBody()), exchange, replyContext, routeKey);

//        spring_returned_message_correlation：所退回消息的唯一标识，可用于更新自定义的消息记录表
        log.info("消息的唯一标识：" + message.getMessageProperties().getHeader("spring_returned_message_correlation" ).toString());
    }


    //订单结果普通队列配置-Fanout
    public static final String SECKILL_ORDER_RES_FANOUT_EXCHANGE_NAME = "seckill.ordeRes.fanout.exchange";
    public static final String SECKILL_ORDER_RES_FANOUT_QUEUE_NAME = "seckill.ordeRes.fanout.queue";

    // 声明交换机，默认是持久化-Fanout
    @Bean("seckillOrderResFanoutExchange")
    public FanoutExchange seckillOrderResFanoutExchange() {
        return new FanoutExchange(SECKILL_ORDER_RES_FANOUT_EXCHANGE_NAME);
    }
    // 声明队列-Fanout
    @Bean("seckillOrderResFanoutQueue")
    public Queue seckillOrderResFanoutQueue() {
        return new Queue(SECKILL_ORDER_RES_FANOUT_QUEUE_NAME);
    }
    // 声明队列的绑定关系-Fanout
    @Bean
    public Binding seckillOrderResFanoutBinding(@Qualifier("seckillOrderResFanoutQueue") Queue queue,
                                                @Qualifier("seckillOrderResFanoutExchange") FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }



    //订单结果普通队列配置-Routing
//    public static final String SECKILL_ORDER_RES_EXCHANGE_NAME = "seckill.ordeRes.exchange";
//    public static final String SECKILL_ORDER_RES_QUEUE_NAME = "seckill.ordeRes.queue";
//    public static final String SECKILL_ORDER_RES_QUEUE_ROUTING_KEY = "seckill.ordeRes.queue.routingkey";
//    // 声明交换机，默认是持久化
//    @Bean("seckillOrderResExchange")
//    public DirectExchange seckillOrderResExchange() {
//        return new DirectExchange(SECKILL_ORDER_RES_EXCHANGE_NAME);
//    }
//    // 声明队列
//    @Bean("seckillOrderResQueue")
//    public Queue seckillOrderResQueue() {
//        return new Queue(SECKILL_ORDER_RES_QUEUE_NAME);
//    }
//
//    // 声明队列的绑定关系
//    @Bean
//    public Binding seckillOrderResBinding(@Qualifier("seckillOrderResQueue") Queue queue,
//                                  @Qualifier("seckillOrderResExchange") DirectExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(SECKILL_ORDER_RES_QUEUE_ROUTING_KEY);
//    }


    //订单结果普通队列配置-Topic
//    public static final String SECKILL_ORDER_RES_TOPIC_EXCHANGE_NAME = "seckill.ordeRes.topic.exchange";
//    public static final String SECKILL_ORDER_RES_TOPIC_QUEUE_NAME = "seckill.ordeRes.topic.queue";
//    public static final String SECKILL_ORDER_RES_TOPIC_QUEUE_ROUTING_KEY = "seckill.ordeRes.topic.queue.#.routingkey";
//    // 声明交换机，默认是持久化-Topic
//    @Bean("seckillOrderResTopicExchange")
//    public TopicExchange seckillOrderResTopicExchange() {
//        return new TopicExchange(SECKILL_ORDER_RES_TOPIC_EXCHANGE_NAME);
//    }
//    // 声明队列-Topic
//    @Bean("seckillOrderResTopicQueue")
//    public Queue seckillOrderResTopicQueue() {
//        return new Queue(SECKILL_ORDER_RES_TOPIC_QUEUE_NAME);
//    }
//    // 声明队列的绑定关系-Topic
//    @Bean
//    public Binding seckillOrderResTopicBinding(@Qualifier("seckillOrderResTopicQueue") Queue queue,
//                                  @Qualifier("seckillOrderResTopicExchange") TopicExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(SECKILL_ORDER_RES_TOPIC_QUEUE_ROUTING_KEY);
//    }





    //秒杀普通队列配置
    public static final String SECKILL_PRE_ORDER_EXCHANGE_NAME = "seckill.preOrder.exchange";
    public static final String SECKILL_PRE_ORDER_QUEUE_NAME = "seckill.preOrder.queue";
    public static final String SECKILL_PRE_ORDER_QUEUE_ROUTING_KEY = "seckill.preOrder.queue.routingkey";

    // 声明交换机，默认是持久化
    @Bean("seckillPreOrderExchange")
    public DirectExchange seckillPreOrderExchange() {
        return new DirectExchange(SECKILL_PRE_ORDER_EXCHANGE_NAME);
    }

    // 声明队列
    @Bean("seckillPreOrderQueue")
    public Queue seckillPreOrderQueue() {
        return new Queue(SECKILL_PRE_ORDER_QUEUE_NAME);
    }

    // 声明队列的绑定关系
    @Bean
    public Binding seckillPreOrderBinding(@Qualifier("seckillPreOrderQueue") Queue queue,
                                  @Qualifier("seckillPreOrderExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(SECKILL_PRE_ORDER_QUEUE_ROUTING_KEY);
    }



    //秒杀延迟队列配置
    // 声明 2个路由key 2个队列 2个交换机
    // 延迟交换机
    public static final String SECKILL_PRE_ORDER_DELAY_EXCHANGE_NAME = "seckill.delay.exchange";

    // 延迟队列
    public static final String SECKILL_PRE_ORDER_DELAY_QUEUE_NAME = "seckill.delay.queue";

    // 延迟队列路由key
    public static final String SECKILL_PRE_ORDER_DELAY_QUEUE_ROUTING_KEY = "seckill.delay.queue.routingkey";

    // 死信交换机
    public static final String SECKILL_PRE_ORDER_DEAD_LETTER_EXCHANGE_NAME = "dead.letter.exchange";

    // 死信队列
    public static final String SECKILL_PRE_ORDER_DEAD_LETTER_QUEUE_NAME = "seckill.dead.letter.queue";

    // 死信队列路由key
    public static final String SECKILL_PRE_ORDER_DEAD_LETTER_QUEUE_ROUTING_KEY = "seckill.dead.letter.delay_30m.routingkey";

    // 声明延迟交换机，默认是持久化
    @Bean("seckillPreOrderDelayExchange")
    public DirectExchange seckillPreOrderDelayExchange() {
        return new DirectExchange(SECKILL_PRE_ORDER_DELAY_EXCHANGE_NAME);
    }

    // 声明延迟队列，延迟30m，并且绑定到对应的死信交换机
    @Bean("seckillPreOrderDelayQueue")
    public Queue seckillDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        // x-dead-letter-exchange 声明队列绑定的死信交换机
        args.put("x-dead-letter-exchange", SECKILL_PRE_ORDER_DEAD_LETTER_EXCHANGE_NAME);
        // x-dead-letter-routing-key 声明队列的死信路由Key
        args.put("x-dead-letter-routing-key", SECKILL_PRE_ORDER_DEAD_LETTER_QUEUE_ROUTING_KEY);
        // 声明队列的消息 TTL 存活时间
        args.put("x-message-ttl", 1800000);
//        args.put("x-message-ttl", 6000);
        return QueueBuilder.durable(SECKILL_PRE_ORDER_DELAY_QUEUE_NAME).withArguments(args).build();
    }

    // 声明延迟队列的绑定关系
    @Bean
    public Binding seckillPreOrderDelayBinding(@Qualifier("seckillPreOrderDelayQueue") Queue queue,
                                       @Qualifier("seckillPreOrderDelayExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(SECKILL_PRE_ORDER_DELAY_QUEUE_ROUTING_KEY);
    }

    // 声明死信交换机
    @Bean("seckillPreOrderDeadLetterExchange")
    public DirectExchange seckillPreOrderDeadLetterExchange() {
        return new DirectExchange(SECKILL_PRE_ORDER_DEAD_LETTER_EXCHANGE_NAME);
    }

    // 声明死信队列 用于接收延迟10s处理的消息
    @Bean("seckillPreOrderDeadLetterQueue")
    public Queue seckillPreOrderDeadLetterQueueA() {
        return new Queue(SECKILL_PRE_ORDER_DEAD_LETTER_QUEUE_NAME);
    }


    // 声明死信队列的绑定关系
    @Bean
    public Binding seckillPreOrderDeadLetterBindingA(@Qualifier("seckillPreOrderDeadLetterQueue") Queue queue,
                                             @Qualifier("seckillPreOrderDeadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(SECKILL_PRE_ORDER_DEAD_LETTER_QUEUE_ROUTING_KEY);
    }


    //邮件激活功能延迟队列配置
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
    @Bean("delayExchange" )
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE_NAME);
    }

    // 声明死信交换机
    @Bean("deadLetterExchange" )
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE_NAME);
    }

    // 声明延迟队列A，延迟30m，并且绑定到对应的死信交换机
    @Bean("delayQueueA" )
    public Queue delayQueueA() {
        Map<String, Object> args = new HashMap<>();
        // x-dead-letter-exchange 声明队列绑定的死信交换机
        args.put("x-dead-letter-exchange" , DEAD_LETTER_EXCHANGE_NAME);
        // x-dead-letter-routing-key 声明队列的死信路由Key
        args.put("x-dead-letter-routing-key" , DEAD_LETTER_QUEUE_A_ROUTING_KEY);
        // 声明队列的消息 TTL 存活时间
        args.put("x-message-ttl" , 1800000);
//        args.put("x-message-ttl" , 6000);
        return QueueBuilder.durable(DELAY_QUEUE_A_NAME).withArguments(args).build();
    }

    // 声明延迟队列B，延迟60m，并且绑定到对应的死信交换机
    @Bean("delayQueueB" )
    public Queue delayQueueB() {
        Map<String, Object> args = new HashMap<>();
        // x-dead-letter-exchange 声明队列绑定的死信交换机
        args.put("x-dead-letter-exchange" , DEAD_LETTER_EXCHANGE_NAME);
        // x-dead-letter-routing-key 声明队列的死信路由Key
        args.put("x-dead-letter-routing-key" , DEAD_LETTER_QUEUE_B_ROUTING_KEY);
        // 声明队列的消息 TTL 存活时间
        args.put("x-message-ttl" , 3600000);
        return QueueBuilder.durable(DELAY_QUEUE_B_NAME).withArguments(args).build();
    }

    // 声明延迟队列A的绑定关系
    @Bean
    public Binding delayBindingA(@Qualifier("delayQueueA" ) Queue queue,
                                 @Qualifier("delayExchange" ) DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DELAY_QUEUE_A_ROUTING_KEY);
    }

    // 声明延迟队列B的绑定关系
    @Bean
    public Binding delayBindingB(@Qualifier("delayQueueB" ) Queue queue,
                                 @Qualifier("delayExchange" ) DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DELAY_QUEUE_B_ROUTING_KEY);
    }

    // 声明死信队列A 用于接收延迟10s处理的消息
    @Bean("deadLetterQueueA" )
    public Queue deadLetterQueueA() {
        return new Queue(DEAD_LETTER_QUEUE_A_NAME);
    }

    // 声明死信队列A 用于接收延迟60m处理的消息
    @Bean("deadLetterQueueB" )
    public Queue deadLetterQueueB() {
        return new Queue(DEAD_LETTER_QUEUE_B_NAME);
    }

    // 声明死信队列A的绑定关系
    @Bean
    public Binding deadLetterBindingA(@Qualifier("deadLetterQueueA" ) Queue queue,
                                      @Qualifier("deadLetterExchange" ) DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEAD_LETTER_QUEUE_A_ROUTING_KEY);
    }

    // 声明死信队列B的绑定关系
    @Bean
    public Binding deadLetterBindingB(@Qualifier("deadLetterQueueB" ) Queue queue,
                                      @Qualifier("deadLetterExchange" ) DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEAD_LETTER_QUEUE_B_ROUTING_KEY);
    }


}
