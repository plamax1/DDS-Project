package com.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Recv {

    private final static String EXCHANGE_NAME = "topic_exchange";
    private final static String endMessage = "end";
    private int msgNumber;
    private int delaySum;
    private long startTransmissionTime;

    public long getStartTransmissionTime() {
        return startTransmissionTime;
    }

    public void setStartTransmissionTime(long startTransmissionTime) {
        this.startTransmissionTime = startTransmissionTime;
    }

    public int getDelaySum() {
        return delaySum;
    }

    public void setDelaySum(int delaySum) {
        this.delaySum = delaySum;
    }

    public void incDelaySum(int delay) {
        this.delaySum += delay;
    }

    public int getMsgNumber() {
        return msgNumber;
    }

    public void setMsgNumber(int msgNumber) {
        this.msgNumber = msgNumber;
    }

    public void incMsgNumber() {
        this.msgNumber = msgNumber + 1;
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // connect to the exchanger and get the queue name
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();

        // get the environmant variable topic
        String bindingKey = System.getenv("topic");
        channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);

        channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);

        Recv r = new Recv();
        r.setMsgNumber(0);
        r.setDelaySum(0);
        System.out.println("[*] Waiting for messages. Topic is " + bindingKey + ". To exit press CTRL+C.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            if (!message.equals(endMessage)) {
                long rcvInstant = (long) delivery.getProperties().getHeaders().get("timestamp");
                long delay = System.currentTimeMillis() - rcvInstant;
                System.out.println(r.getMsgNumber() + "] Delay:[" + delay + " ms]Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
                r.incMsgNumber();
                r.incDelaySum((int) delay);
            } else {
                float avgDelay = (float)(((float)r.getDelaySum()) / ((float)r.getMsgNumber()));
                long endTime = System.currentTimeMillis();
                System.out.println(endMessage + " RECEIVED! The average delay is " + avgDelay + "ms.");
                long difference = endTime - r.getStartTransmissionTime();
                float throughput = (float)( (float)r.getMsgNumber() / (float)((float)1000 * (difference)));
                System.out.println("You received " + r.getMsgNumber() + " messages in " + difference + "ms. So you received " + Math.pow(throughput, -1) +" messages per second.");
                r.setDelaySum(0);
                r.setMsgNumber(0);
            }
            if (r.getMsgNumber() == 1) {
                r.setStartTransmissionTime(System.currentTimeMillis());
            }
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}