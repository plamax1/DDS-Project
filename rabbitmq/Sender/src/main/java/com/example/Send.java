package com.example;

import java.util.Date;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {

    private final static String QUEUE_NAME = "topic_queue";
    private final static String EXCHANGE_NAME = "topic_exchange";
    private final static String message = "Pelphs wins again!";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        String bindingKey = System.getenv("topic");
        int messagesToSend = Integer.parseInt(System.getenv("messagesToSend"));

        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            for (int i = 0; i < messagesToSend; i++) {

                AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().timestamp(new Date()).build();

                channel.basicPublish(EXCHANGE_NAME, bindingKey, properties, message.getBytes("UTF-8"));

                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                System.out.println(i + "] Sent '" + bindingKey + "':'" + message + "'");
            }
        }
    }
}