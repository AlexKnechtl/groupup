package com.example.alexander.groupup.models;

public class MessagesModel {

    public String message, from, time, name, type, id, date;

    public MessagesModel(String message, String name, String time, String from, String type, String id, String date) {
        this.message = message;
        this.name = name;
        this.time = time;
        this.from = from;
        this.type = type;
        this.id = id;
        this.date = date;
    }

    public MessagesModel() {
    }
}