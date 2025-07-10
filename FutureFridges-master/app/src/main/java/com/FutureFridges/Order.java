package com.FutureFridges;

import com.google.firebase.Timestamp;
import java.util.Calendar;

public class Order {
    private String id;
    private String foodName;
    private int quantity;
    private String status;
    private Timestamp expiryDate;


    public Order() {}


    public Order(String foodName, int quantity, Timestamp expiryDate) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.status = "Not Delivered Yet";
        this.expiryDate = expiryDate != null ? expiryDate : getDefaultExpiryDate();
    }

    public Order(String foodName, int quantity) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.status = "Not Delivered Yet";
        this.expiryDate = getDefaultExpiryDate();
    }

    //
    private Timestamp getDefaultExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        return new Timestamp(calendar.getTime());
    }


    public String getId() { return id; }
    public String getFoodName() { return foodName; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public Timestamp getExpiryDate() { return expiryDate; }


    public void setId(String id) { this.id = id; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setStatus(String status) { this.status = status; }
    public void setExpiryDate(Timestamp expiryDate) { this.expiryDate = expiryDate; }
}
