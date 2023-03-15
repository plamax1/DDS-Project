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
    private final static String DEFAULT_TOPIC = "sport";
    private final static String DEFAULT_MESSAGE_BODY = "Hello";
    private final static String endMessage = "end";
    private static String producerId;

    public static void main(String[] argv) throws Exception {
        // Generate a random identifier for this producer.
        producerId = java.util.UUID.randomUUID().toString();
        System.out.println("Producer ID: [" + producerId + "]");

        // Create a connection with the Broker
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        String topic = getTopic();
        int messagesToSend = getMessageToSend();

        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            long lasttime = System.nanoTime();
            long startTime = lasttime;
            long timestamp = 0;

            String poisson = System.getenv("poisson");
            long rate = getMessageRate(poisson);

            // rabbitmq sends message body as byte[]
            byte[] messageContent = DEFAULT_MESSAGE_BODY.getBytes("UTF-8");

            // don't use poisson
            if (poisson.equals("N") || poisson == null) {
                System.out.println(producerId + "] Sending " + messagesToSend + " messages containing the body '"
                        + DEFAULT_MESSAGE_BODY + "' on topic '" + topic + "'. Delay between 2 messages is "
                        + rate / (10 * 10 * 10 * 10 * 10 * 10)
                        + " ms.");
                for (int i = 0; i < messagesToSend; i++) {

                    timestamp = System.nanoTime();
                    // create a new message property containing the timestamp
                    Map<String, Object> messageProperties = messageTimestamp(timestamp);

                    channel.basicPublish(EXCHANGE_NAME, topic,
                            new AMQP.BasicProperties.Builder().headers(messageProperties).build(),
                            messageContent);

                    // VERBOSE
                    verbose(i, timestamp, lasttime);

                    lasttime = timestamp;
                    wait(timestamp, rate);
                }
            }
            // use poisson
            else {
                ArrayList<Double> sleeps = new ArrayList<Double>();
                sleeps = poissonGenerator(Integer.parseInt(poisson), messagesToSend);

                for (int i = 0; i < messagesToSend - 1; i++) {
                    timestamp = System.nanoTime();
                    // create a new message property containing the timestamp
                    Map<String, Object> messageProperties = messageTimestamp(timestamp);

                    channel.basicPublish(EXCHANGE_NAME, topic,
                            new AMQP.BasicProperties.Builder().headers(messageProperties).build(),
                            messageContent);

                    /*
                     * //VERBOSE
                     * verbose(i, timestamp, lasttime, sleeps.get(i));
                     */

                    lasttime = timestamp;
                    wait(timestamp, (long) (sleeps.get(i) * 1000000000));

                }
            }

            // Communicate that the transmission is over sending the endMessage.
            Map<String, Object> messageProperties = messageTimestamp(timestamp);
            channel.basicPublish(EXCHANGE_NAME, topic,
                    new AMQP.BasicProperties.Builder().headers(messageProperties).build(),
                    endMessage.getBytes("UTF-8"));

            // print total elapsed time
            System.out.println("Transmission ended. It requires " + (System.nanoTime() - startTime)
                    + "ns (" + ((System.nanoTime() - startTime) / (10 * 10 * 10 * 10 * 10 * 10 * 10 * 10 * 10))
                    + "s) to send " + messagesToSend + " messages.");
        }
    }

    private static Map<String, Object> messageTimestamp(long timestamp) {
        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("timestamp", timestamp);
        messageProperties.put("user-id", producerId);
        return messageProperties;
    }

    private static String getTopic() {
        String topic = System.getenv("topic");
        if (topic == null || topic.equals((String) "")) {
            System.out.println("Environment variable 'topic' not found. Sending message with the default topic '"
                    + DEFAULT_TOPIC + "'.");
            return DEFAULT_TOPIC;
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

    public static ArrayList<Double> poissonGenerator(int lambda, int n) {
        double time_span = n / lambda;
        ArrayList<Double> events = new ArrayList<Double>();
        ArrayList<Double> sleeps = new ArrayList<Double>();
        for (int j = 0; j < n; j++) {
            events.add(Math.random());
        }
        Collections.sort(events);
        for (int j = 0; j < n; j++) {
            events.set(j, events.get(j) * time_span);
        }
        for (int i = 1; i < events.size(); i++) {
            sleeps.add(events.get(i) - events.get(i - 1));
        }
        return sleeps;
    }

    private static long getMessageRate(String poisson) {
        // use rate only if not using poisson
        if (poisson.equals("N")) {

            String messageRate = System.getenv("rate");
            // Check if rate is not valid
            if (messageRate == null || messageRate.equals((String) "")) {
                System.out.println("Environment variable 'messagesRate' not found. Sending messages without delays.");
                return 0;
            }

            int msgSec = Integer.parseInt(messageRate);
            System.out.println("Input msg/s is " + msgSec);
            // number of messages to send for each nano sec
            float msgNanoSec = (float) ((float) msgSec) / ((float) (10 * 10 * 10 * 10 * 10 * 10 * 10 * 10 * 10));
            System.out.println("number of messages to send for each nanoSecond: " + msgNanoSec);
            long delay = (long) ((float) 1 / msgNanoSec); // delay in nanoSec between 2 messages
            System.out.println("Delay between two messages set to " + delay + "ns ("
                    + ((double) msgNanoSec / (double) (10 * 10 * 10 * 10 * 10 * 10)) + " ms)");
            return delay;
        } else
            return -1;
    }

    public static void wait(long startTime, long waitTime) {
        long target = startTime + waitTime;
        while (System.nanoTime() < target) {
        }
    }

    // Prints messages sent in constant mode
    public static void verbose(int msgN, long timestamp, long lasttime) {
        System.out.println(msgN + "] Elapsed time: " + (timestamp - lasttime) / (10 * 10 * 10 * 10 * 10 * 10) + "ms.");
    }

    // Prints messages sent in poisson mode
    public static void verbose(int msgN, long timestamp, long lasttime, double poissonValue) {
        System.out.println(msgN + "] Poisson Value: " + poissonValue + "]. Elapsed time: "
                + (timestamp - lasttime) / (10 * 10 * 10 * 10 * 10 * 10) + "ms.");
    }

}