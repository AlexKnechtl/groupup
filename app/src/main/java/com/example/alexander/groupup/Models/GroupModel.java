package com.example.alexander.groupup.Models;

public class GroupModel {

    public String category;
    public String location;
    public String tag;
    public String group_image;

    public GroupModel() {

    }

    public GroupModel(String category, String location, String kind) {
        this.category = category;
        this.location = location;
        this.tag = tag;
    }

    public String getCategory() {
        return category;
    }

    public void setCategoryCity(String category, String location) {
        this.category = category;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getGroup_image() {
        return group_image;
    }

    public void setGroup_image(String group_image) {
        this.group_image = group_image;
    }
}
