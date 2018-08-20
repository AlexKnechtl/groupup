package com.example.alexander.groupup.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;

public class GroupModel implements Serializable {

    public GroupModel(String activity, String location, String tag1, String tag2, String tag3, String group_image, GroupType category, PublicStatus public_status, String description, String latlng, HashMap<String, GroupMember> members) {
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
    public GroupType category;
    public PublicStatus public_status;
    public String description;
    public String latlng;
    public HashMap<String, GroupMember> members;

    public GroupModel() {
    }

    @Exclude
    public Integer getMemberCount() {
        if (members != null)
            return members.size();
        else
            return 0;
    }
}