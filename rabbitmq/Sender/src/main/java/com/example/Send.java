package com.example;

import java.util.HashMap;
import java.util.Map;

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

                Map<String, Object> messageProperties = new HashMap<>();
                messageProperties.put("timestamp", System.currentTimeMillis());

                channel.basicPublish(EXCHANGE_NAME, bindingKey, new AMQP.BasicProperties.Builder().headers(messageProperties).build(), message.getBytes("UTF-8"));

                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                System.out.println(messageProperties.get("timestamp") + "] Sent '" + bindingKey + "':'" + message + "'");
            }
        }
    }
}