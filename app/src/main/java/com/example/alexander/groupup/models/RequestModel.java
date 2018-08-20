package com.example.alexander.groupup.models;

public class RequestModel {

    private String name, thumb_image, type, from;
    private Long time;

    public RequestModel(String name, String thumb_image, String type, String from, Long time) {
        this.name = name;
        this.type = type;
        this.thumb_image = thumb_image;
        this.from = from;
        this.time = time;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public RequestModel() {
    }
}