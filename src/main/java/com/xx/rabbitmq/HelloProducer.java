package com.xx.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class HelloProducer {

    public static String QUEUE_NAME = "HELLO_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory  = new ConnectionFactory();
        connectionFactory.setHost("192.168.11.47");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123456");

        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        channel.basicPublish("",QUEUE_NAME,null,"hello".getBytes());



    }


}
