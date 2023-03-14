package org.example.models;

import java.io.Serializable;

public class Message implements Serializable {
    public int id;
    public String message;

    public long timestamp;

    public Message(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }
}
