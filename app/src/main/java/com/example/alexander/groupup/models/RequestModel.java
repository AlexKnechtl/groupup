package com.example.alexander.groupup.models;

public class RequestModel {

    private String name, thumb_image, type, from, group_id;

    public RequestModel(String name, String thumb_image, String type, String from, String group_id) {
        this.name = name;
        this.type = type;
        this.thumb_image = thumb_image;
        this.from = from;
        this.group_id = group_id;
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

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public RequestModel() { }
}
