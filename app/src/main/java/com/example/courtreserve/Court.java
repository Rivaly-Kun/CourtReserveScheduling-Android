package com.example.courtreserve;

import java.util.List;

public class Court {
    private String id;
    private String courtName;
    private String location;
    private String status;
    private List<String> images;
    private double rate; // Added hourly rate

    public Court() {}

    public Court(String id, String courtName, String location, String status, List<String> images, double rate) {
        this.id = id;
        this.courtName = courtName;
        this.location = location;
        this.status = status;
        this.images = images;
        this.rate = rate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCourtName() {
        return courtName;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getImages() {
        return images;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
