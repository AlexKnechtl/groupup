package com.example.alexander.groupup.models;

public class GroupModel {

    public String activity;
    public String location;
    public String tag;
    public String group_image;

    public GroupModel() {

    }

    public GroupModel(String activity, String location, String kind) {
        this.activity = activity;
        this.location = location;
        this.tag = tag;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) { this.activity = activity; }

    public void setActivityCity(String activity, String location) {
        this.activity = activity;
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
