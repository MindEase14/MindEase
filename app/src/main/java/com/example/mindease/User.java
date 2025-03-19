package com.example.mindease;

public class User {
    private String name;
    private String timestamp;
    private String userId;
    private String evaluationKey;

    public User(String name, String timestamp, String userId, String evaluationKey) {
        this.name = name;
        this.timestamp = timestamp;
        this.userId = userId;
        this.evaluationKey = evaluationKey;
    }

    public String getName() { return name; }
    public String getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }
    public String getEvaluationKey() { return evaluationKey; }
}