package com.realEstate.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String buyerId;
    private String sellerId;
    private String propertyId;
    private double price;
    private String cardNumber;
    private String cardType;
    private String cardExpiryDate;
    private String cardCVV;
    private LocalDateTime time;

    public Transaction() {}

    public Transaction(String transactionId, String buyerId, String sellerId, String propertyId, double price, String cardNumber, String cardType, String cardExpiryDate, String cardCVV, LocalDateTime time) {
        this.transactionId = transactionId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.propertyId = propertyId;
        this.price = price;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.cardExpiryDate = cardExpiryDate;
        this.cardCVV = cardCVV;
        this.time = time;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardExpiryDate() {
        return cardExpiryDate;
    }

    public void setCardExpiryDate(String cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }

    public String getCardCVV() {
        return cardCVV;
    }

    public void setCardCVV(String cardCVV) {
        this.cardCVV = cardCVV;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
