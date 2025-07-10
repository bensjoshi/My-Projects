package com.FutureFridges;

import com.google.firebase.Timestamp;

public class DeliveryRecord {
    private String foodName;
    private int quantity;
    private Timestamp timestamp;

    // Required no-arg constructor for Firestore
    public DeliveryRecord() {}

    public DeliveryRecord(String foodName, int quantity, Timestamp timestamp) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getFoodName() {
        return foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
