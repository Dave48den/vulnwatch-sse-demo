package com.example.vulnwatchssedemo.model;

public class Scan {

    private String id;
    private String status;

    public Scan(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}