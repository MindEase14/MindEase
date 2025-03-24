package com.example.mindease;

public class HistoryItem {
    private String formattedDate;
    private long timestamp;
    private String recordKey;

    public HistoryItem(String formattedDate, long timestamp, String recordKey) {
        this.formattedDate = formattedDate;
        this.timestamp = timestamp;
        this.recordKey = recordKey;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getRecordKey() {
        return recordKey;
    }
}