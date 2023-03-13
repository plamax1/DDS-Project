package org.example.models;


public class Message {
    private static int nextId=0;
    public int id;
    public String message;

    public long timestamp;


    public Message(String message) {
        id = nextId++;
        this.message = message;

    }
}