package com.xx.rabbitmqdemo.config;

import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Slf4j
@Configuration
public class RabbitMqConfig {

    public static final String BUSINESS_QUEUE = "BUSINESS_QUEUE";
    public static final String BUSINESS_EXCHANGE = "BUSINESS_EXCHANGE";
    public static final String BUSINESS_DEAD_LETTER_EXCHANGE = "BUSINESS_DEAD_LETTER_EXCHANGE";
    public static final String BUSINESS_DEAD_LETTER_QUEUE = "BUSINESS_DEAD_LETTER_QUEUE";

    //带有TTL的queue
    public static final String TTL_QUEUE = "TTL_QUEUE";
    public static final String TTL_EXCHANGE = "TTL_EXCHANGE";
    public static final String TTL_DEAD_LETTER_QUEUE = "TTL_DEAD_LETTER_QUEUE";
    public static final String TTL_DEAD_LETTER_EXCHANGE = "TTL_DEAD_LETTER_EXCHANGE";

    //不为Queue设置TTL，TTL时间由生产者决定
    public static final String QUEUE = "QUEUE";
    public static final String QUEUE_ROUTINGKEY = "com.xx.queue.routingKey";

    public static final String BUSINESS_ROUTINGKEY = "com.xx.business.routingKey";
    public static final String BUSINESS_DEAD_LETTER_ROUTINGKEY = "com.xx.business.deadLetter.routingKey";
    public static final String TTL_ROUTINGKEY = "com.xx.ttl.routingKey";
    public static final String TTL_DEAD_LETTER_ROUTINGKEY = "com.xx.ttl.deadLetter.routingKey";


    @Bean("businessExchange")
    public Exchange businessExchange(){
        return ExchangeBuilder.directExchange(BUSINESS_EXCHANGE).durable(true).build();
    }

    @Bean("businessDeadLetterExchange")
    public Exchange businessDeadLetterExchange(){
        return ExchangeBuilder.directExchange(BUSINESS_DEAD_LETTER_EXCHANGE).durable(true).build();
    }

    @Bean("ttlExchange")
    public Exchange ttlExchange(){
        return ExchangeBuilder.directExchange(TTL_EXCHANGE).durable(true).build();
    }

    @Bean("ttlDeadLetterExchange")
    public Exchange ttlDeadLetterExchange(){
        return ExchangeBuilder.directExchange(TTL_DEAD_LETTER_EXCHANGE).durable(true).build();
    }


    @Bean("businessQueue")
    public Queue businessQueue(){
        // 声明Queue 绑定死信交换机  指定死信队列RoutingKey
        return QueueBuilder.durable(BUSINESS_QUEUE).deadLetterExchange(BUSINESS_DEAD_LETTER_EXCHANGE).deadLetterRoutingKey(BUSINESS_DEAD_LETTER_ROUTINGKEY).build();
    }

    @Bean("queue")
    public Queue queue(){
        // 声明Queue 绑定死信交换机  指定死信队列RoutingKey
        return QueueBuilder.durable(QUEUE).deadLetterExchange(TTL_DEAD_LETTER_EXCHANGE).deadLetterRoutingKey(TTL_DEAD_LETTER_ROUTINGKEY).build();
    }

    @Bean("ttlQueue")
    public Queue ttlQueue(){
        //声明Queue  绑定死信交换机  指定死信队列RoutingKey  为Queue指定 消息的TTL 10s
        return QueueBuilder.durable(TTL_QUEUE).deadLetterExchange(TTL_DEAD_LETTER_EXCHANGE).deadLetterRoutingKey(TTL_DEAD_LETTER_ROUTINGKEY).ttl(10000).build();
    }

    @Bean("businessDeadLetterQueue")
    public Queue businessDeadLetterQueue(){
        return QueueBuilder.durable(BUSINESS_DEAD_LETTER_QUEUE).build();
    }

    @Bean("ttlDeadLetterQueue")
    public Queue ttlDeadLetterQueue(){
        return QueueBuilder.durable(TTL_DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding queueBinding(
            @Qualifier("queue") Queue queue,
            @Qualifier("ttlExchange")Exchange exchange
    ){
        return BindingBuilder.bind(queue).to(exchange).with(QUEUE_ROUTINGKEY).noargs();
    }

    @Bean
    public Binding businessBinding(
            @Qualifier("businessQueue") Queue queue,
            @Qualifier("businessExchange")Exchange exchange
    ){
        return BindingBuilder.bind(queue).to(exchange).with(BUSINESS_ROUTINGKEY).noargs();
    }


    @Bean
    public Binding businessDeadLetterBinding(
            @Qualifier("businessDeadLetterQueue")Queue queue,
            @Qualifier("businessDeadLetterExchange")Exchange exchange
    ){
        return BindingBuilder.bind(queue).to(exchange).with(BUSINESS_DEAD_LETTER_ROUTINGKEY).noargs();
    }

    @Bean
    public Binding ttlBinding(
            @Qualifier("ttlQueue") Queue queue,
            @Qualifier("ttlExchange")Exchange exchange
    ){
        return BindingBuilder.bind(queue).to(exchange).with(TTL_ROUTINGKEY).noargs();
    }

    @Bean
    public Binding ttlDeadLetterBinding(
            @Qualifier("ttlDeadLetterQueue")Queue queue,
            @Qualifier("ttlDeadLetterExchange")Exchange exchange
    ){
        return BindingBuilder.bind(queue).to(exchange).with(TTL_DEAD_LETTER_ROUTINGKEY).noargs();
    }


}
