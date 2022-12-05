package org.example;

import org.example.messaging.MConsumer;

public class Main {
    public static void main(String[] args) {
        MConsumer.initialize("tcp://127.0.0.1:61616", "messages");

        int c;
        try {
            c = System.in.read();
        } catch (Exception ex) {
        }

        MConsumer.terminate();
    }
}