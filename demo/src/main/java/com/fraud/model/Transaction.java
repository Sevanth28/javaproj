package com.fraud.model;

public class Transaction {

    public double amount;
    public String location;
    public long time;

    public Transaction(double amount, String location) {
        this.amount = amount;
        this.location = location.trim().toLowerCase();
        this.time = System.currentTimeMillis();
    }
}
