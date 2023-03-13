package org.example;

import org.example.messaging.MConsumer;
import org.example.models.Message;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        //String topic = "d";
        String topic = args[0];
        System.out.println("Name of the topic: " + topic);
        MConsumer.initialize("tcp://localhost:61616", topic);


        int c;
        try {
            c = System.in.read();
        } catch (Exception ex) {
        }

        //MConsumer.terminate();
    }
}