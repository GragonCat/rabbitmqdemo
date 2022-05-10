package com.xx.rabbitmqdemo.business.controller;

import com.xx.rabbitmqdemo.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

@Slf4j
@RestController
public class ProducerController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("business/{msg}")
    public String product(@PathVariable String msg){
        System.out.println("business收到信息："+msg);
        rabbitTemplate.convertAndSend(RabbitMqConfig.BUSINESS_EXCHANGE,RabbitMqConfig.BUSINESS_ROUTINGKEY,msg);
        return "ok";
    }

    @RequestMapping("queueTtl/{msg}")
    public String queueTtl(@PathVariable String msg){
        System.out.println("queueTtl收到信息："+msg);
        rabbitTemplate.convertAndSend(RabbitMqConfig.TTL_EXCHANGE,RabbitMqConfig.TTL_ROUTINGKEY,msg);
        return "ok";
    }

    // rabbitmq的延迟队列有bug，生产者指定ttl的延迟任务 需要排队，需要使用插件
    @RequestMapping("producerTtl/{time}/{msg}")
    public String producerTtl(@PathVariable String msg,@PathVariable String time){
        System.out.println("producerTt收到信息："+msg + "TTL :"+ time);
        rabbitTemplate.convertAndSend(RabbitMqConfig.TTL_EXCHANGE,RabbitMqConfig.QUEUE_ROUTINGKEY,msg,
                (message ->{
                    //设置过期时间
                    message.getMessageProperties().setExpiration(time);
                    return message;
                })
        );
        return "ok";
    }

}
