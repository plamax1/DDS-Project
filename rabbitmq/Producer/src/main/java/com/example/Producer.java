package com.example;

import java.util.ArrayList;
import java.util.Collections;
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

            long lasttime = System.currentTimeMillis();
            long startTime = lasttime;
            long timestamp = 0;
            String poisson= System.getenv("poisson");
            if (poisson.equals("N") || poisson == null) {
                for (int i = 0; i < messagesToSend; i++) {

                    timestamp = System.currentTimeMillis();
                    // create a new message property containing the timestamp
                    Map<String, Object> messageProperties = messageTimestamp(timestamp);

                    channel.basicPublish(EXCHANGE_NAME, bindingKey,
                            new AMQP.BasicProperties.Builder().headers(messageProperties).build(),
                            message.getBytes("UTF-8"));

                    System.out.println("[" + (timestamp - lasttime) + "] Sent '" + bindingKey + "':'" + message + "'");
                    lasttime = timestamp;
                }
            }
            else{
                ArrayList<Double> sleeps = poisson(Integer.parseInt(poisson), messagesToSend);
                for(int i=0; i < messagesToSend; i++){
                    Thread.sleep(Math.round(sleeps.get(i))*1000);
                    timestamp = System.currentTimeMillis();
                    // create a new message property containing the timestamp
                    Map<String, Object> messageProperties = messageTimestamp(timestamp);

                    channel.basicPublish(EXCHANGE_NAME, bindingKey,
                            new AMQP.BasicProperties.Builder().headers(messageProperties).build(),
                            message.getBytes("UTF-8"));

                    System.out.println("[" + (timestamp - lasttime) + "] Sent '" + bindingKey + "':'" + message + "'");
                    lasttime = timestamp;
                }
            }

            // Communicate that the transmission is over sending the endMessage.
            channel.basicPublish(EXCHANGE_NAME, bindingKey, null, endMessage.getBytes("UTF-8"));

            System.out.println("Transmission ended. It requires " + (System.currentTimeMillis() - startTime)
                    + "ms to send " + messagesToSend + " messages.");
        }
    }

    private static Map<String, Object> messageTimestamp(long timestamp) {
        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("timestamp", timestamp);
        return messageProperties;
    }

    private static String getTopic() {
        String topic = System.getenv("topic");
        if (topic == null || topic.equals((String) "")) {
            System.out.println("Environment variable 'topic' not found. Listening on the default [" + DEF_TOPIC + "].");
            return DEF_TOPIC;
        }
        return topic;
    }

    private static int getMessageToSend() {
        String messagesToSend = System.getenv("messagesToSend");
        if (messagesToSend == null || messagesToSend.equals((String) "")) {
            System.out.println("Environment variable 'messagesToSend' not found. Sending only 1 message.");
            return 1;
        }
        return Integer.parseInt(System.getenv("messagesToSend"));
    }

    private static String getMessageString() {
        String message = System.getenv("messageContent");
        if (message == null || message.equals((String) "")) {
            System.out.println("Environment variable 'message' not found. Sending the default message: '"
                    + DEF_MESSAGE_STRING + "'.");
            return DEF_MESSAGE_STRING;
        }
        return message;
    }

    public static ArrayList<Double> poisson(int lampda, int n) {
        double time_span = n / lampda;
        ArrayList<Double> events = new ArrayList<Double>();
        ArrayList<Double> sleeps = new ArrayList<Double>();
        for (int j = 0; j < n; j++) {
            events.add(Math.random());
        }
        Collections.sort(events);
        for (int j = 0; j < n; j++) {
            events.set(j, events.get(j) * time_span);
        }
        sleeps.add(0.0);
        for (int i = 1; i < events.size(); i++) {
            sleeps.add(events.get(i) - events.get(i - 1));
        }
        return sleeps;
    }
}