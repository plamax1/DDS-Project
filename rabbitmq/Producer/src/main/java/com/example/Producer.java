package com.example;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
public class Producer {

    private final static String QUEUE_NAME = "topic_queue";
    private final static String EXCHANGE_NAME = "topic_exchange";
    private final static String DEF_TOPIC = "sport.swimming";
    private final static String DEF_MESSAGE_STRING = "Pelphs wins again!";
    private final static String endMessage = "end";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        String bindingKey = getTopic();
        int messagesToSend = getMessageToSend();
        String message = getMessageString();

        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            
            for (int i = 0; i < messagesToSend; i++) {

                //create a new message property containing the timestamp
                Map<String, Object> messageProperties = messageTimestamp();

                channel.basicPublish(EXCHANGE_NAME, bindingKey, new AMQP.BasicProperties.Builder().headers(messageProperties).build(), message.getBytes("UTF-8"));

                System.out.println(messageProperties.get("timestamp") + "] Sent '" + bindingKey + "':'" + message + "'");
            }

            //Communicate that the transmission is over sending the endMessage.
            channel.basicPublish(EXCHANGE_NAME, bindingKey, null, endMessage.getBytes("UTF-8"));
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        }
    }

    private static Map<String, Object> messageTimestamp(){
        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("timestamp", System.currentTimeMillis());
        return messageProperties;
    }

    private static String getTopic(){
        String topic = System.getenv("topic");
        if(topic == null || topic.equals((String)"")){
            System.out.println("Environment variable 'topic' not found. Listening on the default [" + DEF_TOPIC +"].");
            return DEF_TOPIC;
        }
        return topic;
    }

    private static int getMessageToSend(){
        String messagesToSend = System.getenv("messagesToSend");
        if (messagesToSend==null || messagesToSend.equals((String)"")){
            System.out.println("Environment variable 'messagesToSend' not found. Sending only 1 message.");
            return 1;
        }
        return Integer.parseInt(System.getenv("messagesToSend"));
    }
    private static String getMessageString(){
        String message = System.getenv("message");
        if(message == null || message.equals((String)"")){
            System.out.println("Environment variable 'message' not found. Sending the default message: '" + DEF_MESSAGE_STRING +"'.");
            return DEF_MESSAGE_STRING;
        }
        return message;
    }
}