package com.xx.rabbitmqdemo.business.service;


import com.rabbitmq.client.Channel;
import com.xx.rabbitmqdemo.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class Consumer implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {

    @Autowired
    RabbitTemplate rabbitTemplate;

    //注入确认回调实现和消息回退回调实现
    @PostConstruct
    public void init(){
        rabbitTemplate.setReturnsCallback(this);
        rabbitTemplate.setConfirmCallback(this);
    }

    @RabbitListener(queues = {RabbitMqConfig.BUSINESS_QUEUE})
    public void listen(Message message, Channel channel) throws IOException, InterruptedException {
        String msg = new String(message.getBody(),"UTF-8");
        boolean ack = true;
        if(msg.contains("0")){
            ack = false;
        }
        if(!ack){
            log.info("Business消费者消费：{} 信息发生异常，产生死信",message);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        }else{
            log.info("Business消费者消费：{} 信息成功",msg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
    }


    @RabbitListener(queues = RabbitMqConfig.TTL_DEAD_LETTER_QUEUE)
    public void ttlDeadLetterListen(Message message,Channel channel) throws IOException, InterruptedException {
        String msg = new String(message.getBody(),"UTF-8");
        System.out.println("TTL-死信队列消费信息" + message);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @RabbitListener(queues = RabbitMqConfig.BUSINESS_DEAD_LETTER_QUEUE)
    public void businessDeadLetterListen(Message message,Channel channel) throws IOException, InterruptedException {
        String msg = new String(message.getBody(),"UTF-8");
        System.out.println("business-死信队列消费者消费信息" + message);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println(correlationData.getReturned());
    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {

    }
}
