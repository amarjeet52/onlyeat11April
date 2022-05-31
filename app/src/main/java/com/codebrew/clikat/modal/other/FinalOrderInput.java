package com.codebrew.clikat.modal.other;

public class FinalOrderInput {

    private String orderId;
    private String transactionId;
    private String type;
    private String status;
    private String cardEndingDigits;
    private String token;
    private String scheme;
    private String user_id;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getCardEndingDigits() {
        return cardEndingDigits;
    }

    public void setCardEndingDigits(String cardEndingDigits) {
        this.cardEndingDigits = cardEndingDigits;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

  }
