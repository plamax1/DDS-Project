package org.example;

import org.example.messaging.MConsumer;

public class Main {
    public static void main(String[] args) {

        String topic = System.getenv("TOPIC1");
        System.out.println("Name of the topic: " + topic);
        MConsumer.initialize("tcp://localhost:61616", topic);

        // MConsumer.terminate();
    }
}