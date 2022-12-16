package com.example;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

public class Recv {

    private final static String EXCHANGE_NAME = "topic_exchange";
    private final static String endMessage = "end"; // must be the same declared on sender side.
    private static Channel channel;
    private static Stat stat;

    public static Channel getChannel() {
        return channel;
    }

    public static Stat getStat() {
        return stat;
    }

    public static void setStat(Stat stat) {
        Recv.stat = stat;
    }

    public static void setChannel(Channel channel) {
        Recv.channel = channel;
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
        setStat(new Stat());

        System.out.println("Waiting for messages. Topic is " + bindingKey + ". To exit press CTRL+C.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            if (!message.equals(endMessage)) {
                printAndUpdateStat(delivery, message);
            } else {
                // endMessage received
                computeFinalStat();
            }
            if (getStat().getMsgNumber() == 1) {
                getStat().setStartTransmissionTime(System.currentTimeMillis());
            }
        };

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

    public static void printAndUpdateStat(Delivery delivery, String message) {
        Stat stat = getStat();
        try{
        long rcvInstant = (long) delivery.getProperties().getHeaders().get("timestamp");
        long delay = System.currentTimeMillis() - rcvInstant;
        System.out.println(getStat().getMsgNumber() + "] Delay:[" + delay + " ms] Received '" +
                delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        stat.incMsgNumber();
        stat.incDelaySum((int) delay);
        }
        catch(NullPointerException e){
            System.out.println("Message does not contain the param timestamp!!");
        }
    }

    public static void computeFinalStat() {
        Stat stat = getStat();

        // Print average
        float avgDelay = stat.computeAverageDelay();
        System.out.println(endMessage + " RECEIVED! The average delay is " + avgDelay + "ms.");

        // Print msg/s
        long difference = stat.computeTimeDifference();
        float throughput = stat.computeThroughput(difference);
        System.out.println("You received " + stat.getMsgNumber() + " messages in " + difference
                + "ms. So you received " + Math.pow(throughput, -1) + " messages/s.");

        // keep listening, so restart Stat
        setStat(new Stat());
    }
}