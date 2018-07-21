package com.example.alexander.groupup.models;

public class GroupModel {

    public String activity;
    public String location;
    public String tag1;
    public String tag2;
    public String tag3;
    public String group_image;

    public GroupModel() {

    }

    public GroupModel(String activity, String location, String tag1, String tag2, String tag3) {
        this.activity = activity;
        this.location = location;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
    }

    public String getGroup_image() {
        return group_image;
    }

    public void setGroup_image(String group_image) {
        this.group_image = group_image;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) { this.activity = activity; }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setActivityCity(String activity, String location) {
        this.activity = activity;
        this.location = location;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() { return tag2; }

    public void setTag2(String tag2) { this.tag2 = tag2; }

    public String getTag3() { return tag3; }

    public void setTag3(String tag3) { this.tag3 = tag3; }
}
