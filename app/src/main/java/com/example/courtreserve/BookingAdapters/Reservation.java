package com.example.courtreserve.BookingAdapters;

public class Reservation {
    private String courtId;
    private String courtName;
    private long startTime;
    private long endTime;
    private int payment;
    private String userId;
    private long reservationDate;

    private String startTimeReadable;
    private String endTimeReadable;
    private String id;
    private String paymentStatus;
    public Reservation() {
        // Required for Firebase
    }

    public Reservation(String courtName, String startTimeReadable, String endTimeReadable, int payment) {
        this.courtName = courtName;
        this.startTimeReadable = startTimeReadable;
        this.endTimeReadable = endTimeReadable;
        this.payment = payment;
    }

    // --- Getters and Setters ---

    public String getCourtId() {
        return courtId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setCourtId(String courtId) {
        this.courtId = courtId;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(long reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStartTimeReadable() {
        return startTimeReadable;
    }

    public void setStartTimeReadable(String startTimeReadable) {
        this.startTimeReadable = startTimeReadable;
    }

    public String getEndTimeReadable() {
        return endTimeReadable;
    }

    public void setEndTimeReadable(String endTimeReadable) {
        this.endTimeReadable = endTimeReadable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
