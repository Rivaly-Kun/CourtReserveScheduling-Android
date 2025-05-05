package com.example.courtreserve.BookingAdapters;

public class Reservation {
    public String courtName;
    public String startTimeReadable;
    public String endTimeReadable;
    public int payment; // ✅ New field
    private String id;

    public Reservation() {
        // Needed for Firebase
    }

    public Reservation(String courtName, String startTimeReadable, String endTimeReadable, int payment) {
        this.courtName = courtName;
        this.startTimeReadable = startTimeReadable;
        this.endTimeReadable = endTimeReadable;
        this.payment = payment;
    }

    // Getter and setter for the ID field
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
