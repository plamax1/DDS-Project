package org.example.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.models.Message;

import javax.jms.*;

public class MProducer implements AutoCloseable {
    //allows closing such resources automatically at the end of a block, without having to add a finally block which closes the resource explicitly.
    private String baseUrl; //useful for activeMQ broker

    private ConnectionFactory connectionFactory;//to build an activeMQ connection

    private Connection connection; // connection given by connectionFactory;

    private Session session; //connection's session;

    private Queue queue;

    private MessageProducer producer;

    private boolean inError; //useful to see if i'm in error

    private String error;

    private static MProducer instance; //because i want a static class

    private MProducer(String brokerUrl, String queueName) {
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
            producer = session.createProducer(queue);
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return;
        }
        inError = false;

    }
    private void publish(Message message) {
        //put msg message in the queue
        try {
            ObjectMessage msg = session.createObjectMessage(message.id);
            msg.setIntProperty("message_id", message.id);
            msg.setStringProperty("message", message.message);
            producer.send(msg);

        } catch(Exception ex) {
            inError=true;
            error = ex.getMessage();
        }
    }

    @Override
    public void close() {
        try {
            producer.close();
            session.close();
            connection.close();
        }catch (Exception ignored) {}
    }

    public static boolean inError() {
        return instance.inError;
    }

    public static String error() {
        return instance.error;
    }

    public static void initialize(String baseUrl, String queueName) {
        instance = new MProducer(baseUrl, queueName);
    }

    public static void terminate() {
        //useful to terminate, so we close all the resources
        instance.close();
    }

    public static void add(Message message) {
        //add a new message in the queue
        instance.publish(message);
    }
}