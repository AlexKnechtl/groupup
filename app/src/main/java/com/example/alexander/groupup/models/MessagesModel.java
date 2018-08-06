package com.example.alexander.groupup.models;

public class MessagesModel {

    private String message, from, time, name, type;

    public MessagesModel(String message, String name, String time, String from, String type) {
        this.message = message;
        this.name = name;
        this.time = time;
        this.from = from;
        this.type = type;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public MessagesModel() { }
}