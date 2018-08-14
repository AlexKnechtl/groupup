package com.example.alexander.groupup.models;

import java.util.HashMap;

public class GroupModel {

    public GroupModel(String activity, String location, String tag1, String tag2, String tag3, String group_image, String category, String public_status, String description, String latlng, HashMap<String, GroupMember> members) {
        this.activity = activity;
        this.location = location;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.group_image = group_image;
        this.category = category;
        this.public_status = public_status;
        this.description = description;
        this.latlng = latlng;
        this.members = members;
    }

    public String activity;
    public String location;
    public String tag1;
    public String tag2;
    public String tag3;
    public String group_image;
    public String category;
    public String public_status;
    public String description;
    public String latlng;
    public HashMap<String, GroupMember> members;

    public GroupModel() {}
}
