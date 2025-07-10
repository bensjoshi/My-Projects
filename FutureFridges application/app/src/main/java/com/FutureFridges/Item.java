package com.FutureFridges;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Item {
    private String foodName;
    private String foodId;
    private int quantity;
    private Object expiryDate; // 🔹 Store as Object to handle both String & Timestamp

    public Item() {}

    public Item(String foodName, int quantity, Object expiryDate) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Object getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Object expiryDate) {
        this.expiryDate = expiryDate;
    }

    // 🔹 Convert expiryDate to a Timestamp safely
    @Exclude
    public Timestamp getExpiryTimestamp() {
        if (expiryDate instanceof Timestamp) {
            return (Timestamp) expiryDate;
        } else if (expiryDate instanceof String) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse((String) expiryDate);
                return new Timestamp(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 🔹 Get expiry date as a formatted string
    @Exclude
    public String getFormattedExpiryDate() {
        Timestamp timestamp = getExpiryTimestamp();
        return (timestamp != null) ? timestamp.toDate().toString() : "No expiry date";
    }
}
