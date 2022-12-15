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
        this.msgNumber = msgNumber+1;
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //connect to the exchanger and get the queue name
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();

        
        //get the environmant variable topic
        String bindingKey = System.getenv("topic");
        channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
        
        channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);

        Recv r = new Recv();
        r.setMsgNumber(0);
        r.setDelaySum(0);
        System.out.println("[*] Waiting for messages. Topic is " + bindingKey + ". To exit press CTRL+C.");


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            
            if(!message.equals(endMessage)){
            long rcvInstant= (long)delivery.getProperties().getHeaders().get("timestamp") ;
            long delay = System.currentTimeMillis() - rcvInstant;
            System.out.println(r.getMsgNumber() + "] Delay:["  + delay + " ms]Received '" +            
            delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            r.incMsgNumber();
            r.incDelaySum((int) delay);
            }
            else{
                float avgDelay=r.getDelaySum() / r.getMsgNumber();
                System.out.println(endMessage + " RECEIVED! The average delay is " + avgDelay);
                r.setDelaySum(0);
                r.setMsgNumber(0);
            }
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}