package com.example.mindease;

public class HistoryItem {
    private String date;
    private String description;

    public HistoryItem(String date, String description) {
        this.date = date;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}