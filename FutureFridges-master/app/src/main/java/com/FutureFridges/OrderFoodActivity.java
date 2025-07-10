package com.FutureFridges;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OrderFoodActivity extends AppCompatActivity {
    private EditText foodNameEditText, quantityEditText;
    private Button orderButton;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        foodNameEditText = findViewById(R.id.foodNameEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        orderButton = findViewById(R.id.orderButton);

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = foodNameEditText.getText().toString().trim();
                String quantityStr = quantityEditText.getText().toString().trim();

                if (foodName.isEmpty() || quantityStr.isEmpty()) {
                    Toast.makeText(OrderFoodActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    int quantity = Integer.parseInt(quantityStr);
                    placeOrder(foodName, quantity);
                }
            }
        });
    }

    private void placeOrder(String foodName, int quantity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Order order = new Order(foodName, quantity);

        db.collection("orders").add(order)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    order.setId(documentId);
                    db.collection("orders").document(documentId)
                            .update("id", documentId)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(OrderFoodActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                Calendar calendar = Calendar.getInstance();
                                long currentTimeMillis = System.currentTimeMillis();
                                calendar.setTimeInMillis(currentTimeMillis);
                                addNotification(order, currentTimeMillis);
                                foodNameEditText.getText().clear();
                                quantityEditText.getText().clear();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(OrderFoodActivity.this, "Failed to update order with ID", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OrderFoodActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                });



    }


    private void addNotification(Order order, long timestampMillis) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp timestamp = new Timestamp(new java.util.Date(timestampMillis));
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("text", "Order of " + order.getQuantity() + " " + order.getFoodName() + " has been made");
        notificationData.put("date", timestamp);

        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(OrderFoodActivity.this, "Notification added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(OrderFoodActivity.this, "Failed to add notification", Toast.LENGTH_SHORT).show();
                });
    }

}
