package org.example.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.models.Message;
import org.example.utilities.MessageSizeFetcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.jms.*;
import java.util.ArrayList;

public class MConsumer implements AutoCloseable {
    private String baseUrl;

    private ConnectionFactory connectionFactory;

    private Connection connection;

    private Session session;

    private Topic topic;

    private static ArrayList<Long> delayArray = new ArrayList<Long>();

    private long startTimestamp;

    private long lastTimestamp;

    private double throughput;

    private double delay;

    private int counter = 0;

    private MessageConsumer consumer;

    private boolean inError;

    private String error;

    private static MConsumer instance;

    private boolean first = false;

    private void firstTime(long firstTimestamp) {
        if (first == false) {
            first = true;
            startTimestamp = firstTimestamp;
        }
    }

    private double througput(int counter, long timestamp) {
        lastTimestamp = timestamp;
        if (lastTimestamp == startTimestamp) {
            return 0;
        }
        double i = lastTimestamp - startTimestamp;
        double ii = i / 1000;
        double j = counter;
        // double jj= j*size;
        throughput = j / ii;
        return throughput;
    }

    private MConsumer(String brokerUrl, String topicName) {
        baseUrl = brokerUrl;
        connectionFactory = new ActiveMQConnectionFactory(baseUrl);
        try {
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return;
        }
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return;
        }

        try {
            topic = session.createTopic(topicName);
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return;
        }
        try {
            consumer = session.createConsumer(topic);
            consumer.setMessageListener(message -> {
                try {
                    long localTimestamp = System.currentTimeMillis();
                    firstTime(localTimestamp);
                    // int msgId = message.getIntProperty("message_id");
                    String msgMessage = message.getStringProperty("message");
                    // Message i = new org.example.models.Message(msgId, msgMessage);
                    long msgTimestamp = message.getJMSTimestamp();
                    delay = delay + (localTimestamp - msgTimestamp);
                    // if((msgId+1)%10000==0) {
                    // System.out.println("id: " + msgId + " delivered");
                    // System.out.println( "The size of the message is: " +
                    // getMessageSizeInBytes(i));
                    // }
                    counter++;
                    if (msgMessage.equals("end")) {
                        double averageDelay = delay / counter;
                        double t = througput(counter, localTimestamp);
                        System.out.println("END MESSAGES");
                        System.out.println("The average delay is " + averageDelay);
                        System.out.println("The average throughput until now is: " + t + " messages per second");
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            });
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
    }

    @Override
    public void close() {
        try {
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception ignored) {
        }
    }

    public static void initialize(String baseUrl, String topicName) {
        instance = new MConsumer(baseUrl, topicName);
    }

    public static boolean inError() {
        return instance.inError;
    }

    public static String error() {
        return instance.error;
    }

    public static void terminate() {
        instance.close();
    }
}
