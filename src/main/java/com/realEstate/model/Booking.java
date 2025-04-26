package com.realEstate.model;

import java.time.LocalDate;

public class Booking {
    private String requestId;
    private String buyerId;
    private String propertyId;
    private LocalDate scheduledDate;
    private String status;

    public Booking(){
    }

    public Booking(String requestId, String status, LocalDate scheduledDate, String propertyId, String buyerId) {
        this.requestId = requestId;
        this.status = status;
        this.scheduledDate = scheduledDate;
        this.propertyId = propertyId;
        this.buyerId = buyerId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }
}
