package com.example.alexander.groupup.models;

/**
 * Created by alexk on 26.02.2018.
 */

public class UserModel {

    public String name, age, thumb_image, city, date, message;

    public UserModel() { }

    public UserModel(String name, String age, String thumbmail, String city, String date, String message) {
        this.name = name;
        this.age = age;
        this.thumb_image = thumbmail;
        this.city = city;
        this.date = date;
        this.message = message;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public void setNameAge(String name, String age) {
        this.name = name;
        this.age = age;
    }
}