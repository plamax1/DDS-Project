package org.example;

import org.example.messaging.MProducer;
import org.example.models.Message;

public class Main {
    public static void main(String[] args) {
        MProducer.initialize("tcp://127.0.0.1:61616", "messages");
        Message i = new Message("Hello guys");
        if(MProducer.inError()) {
            System.out.println(MProducer.error());
        }
        else {
            MProducer.add(i);
            System.out.println("message " + i.id + " added to queue");
        }
        MProducer.terminate();
    }
}