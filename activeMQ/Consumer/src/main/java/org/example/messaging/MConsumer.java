package org.example.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.models.Message;

import javax.jms.*;

public class MConsumer implements AutoCloseable{
    private String baseUrl;

    private ConnectionFactory connectionFactory;

    private Connection connection;


    private Session session;

    private Queue queue;

    private MessageConsumer consumer;

    private boolean inError;

    private String error;

    private static MConsumer instance;

    private MConsumer(String brokerUrl, String queueName) {
        baseUrl = brokerUrl;
        connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
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

        try{
            queue = session.createQueue(queueName);
        }catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return;
        }
        try {
            consumer = session.createConsumer(queue);
            consumer.setMessageListener(message -> {
                try {
                    int msgId = message.getIntProperty("message_id");
                    String msgMessage = message.getStringProperty("message");
                    Message i = new org.example.models.Message(msgId, msgMessage);
                    System.out.println("message id: " + i.id + " message: " + i.message);
                }
                catch(Exception ex) {
                    System.out.println(ex);
                }
            });
        }
        catch(Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
    }

    @Override
    public void close() {
        try{
            consumer.close();
            session.close();
            connection.close();
        }catch(Exception ignored) {}
    }

    public static void initialize(String baseUrl, String queueName) {
        instance = new MConsumer(baseUrl, queueName);
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
