package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Consumer {

    private final static String EXCHANGE_NAME = "topic_exchange";
    // must be the same declared on sender side.
    private final static String endMessage = "end"; 
    private static Channel channel;
    private static ArrayList<Stat> producerStats;

    public static Channel getChannel() {
        return channel;
    }

    public static void setChannel(Channel channel) {
        Consumer.channel = channel;
    }

    public static Stat getStat(String UUID) {
        for (int i = 0; i < producerStats.size(); i++) {
            if (UUID == producerStats.get(i).getUUID())
                return producerStats.get(i);
        }
        return null;
    }

    public static void main(String[] argv) throws Exception {
        establishChannel();

        // connect to the exchanger and get the queue name
        getChannel().exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();

        // get the environmant variable topic
        String bindingKey = System.getenv("topic");
        getChannel().queueBind(queueName, EXCHANGE_NAME, bindingKey);

        // instantiate the object for the statistics on trasmission
        producerStats = new ArrayList<Stat>();

        System.out.println("Waiting for messages. Topic is " + bindingKey + ". To exit press CTRL+C.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            String messageUUID = delivery.getProperties().getHeaders().get("user-id").toString();
            boolean UUIDExist = false;
            for (int i = 0; i < producerStats.size(); i++) { // new producer
                if (producerStats.get(i).getUUID().equals(messageUUID)) {
                    UUIDExist = true;
                }
            }

            if (UUIDExist == false) { // create a new producer Stats
                System.out.println("[New producer: " + messageUUID + "]");
                producerStats.add(new Stat(messageUUID));
            }

            for (int i = 0; i < producerStats.size(); i++) {
                if (producerStats.get(i).getUUID().equals(messageUUID)) {
                    if (!message.equals(endMessage)) {
                        producerStats.get(i).updateStat(delivery, message);
                    } else {
                        // endMessage received
                        producerStats.get(i).computeFinalStat();
                    }
                    // it is the first message we are receiving from this producer UUID
                    if (producerStats.get(i).getMsgNumber() == 1) { 
                        producerStats.get(i).setStartTransmissionTime(System.nanoTime());
                    }
                }
            }

        };

        // always consume once delivered
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {

        });
    }

    public static void establishChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        setChannel(channel);
    }
}