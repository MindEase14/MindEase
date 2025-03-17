package com.example.mindease;

public class User {
    private String name;
    private String date;

    public User(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}