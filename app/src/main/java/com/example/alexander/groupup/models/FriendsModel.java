package com.example.alexander.groupup.models;

public class FriendsModel {

    public FriendsModel(String date, String name, String thumb_image) {
        this.date = date;
        this.name = name;
        this.thumb_image = thumb_image;
    }

    public String date;
    public String name;
    public String thumb_image;

    public String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public FriendsModel() {

    }
}