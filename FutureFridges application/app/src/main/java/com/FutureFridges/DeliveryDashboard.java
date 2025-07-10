package com.FutureFridges;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DeliveryDashboard extends AppCompatActivity {
    private RecyclerView recyclerViewOrders, recyclerViewDeliveryHistory;
    private OrderAdapter orderAdapter;
    private DeliveryHistoryAdapter deliveryHistoryAdapter;
    private List<Order> orderList;
    private List<DeliveryRecord> deliveryList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_dashboard);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView for Orders
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        recyclerViewOrders.setAdapter(orderAdapter);

        // Initialize RecyclerView for Delivery History
        recyclerViewDeliveryHistory = findViewById(R.id.recyclerViewDeliveryHistory);
        recyclerViewDeliveryHistory.setLayoutManager(new LinearLayoutManager(this));
        deliveryList = new ArrayList<>();
        deliveryHistoryAdapter = new DeliveryHistoryAdapter(this, deliveryList);
        recyclerViewDeliveryHistory.setAdapter(deliveryHistoryAdapter);

        // Load Data
        loadOrders();            // Load pending orders
        loadDeliveryHistory();   // Load delivery history
    }

    // Fetch pending orders from Firestore
    private void loadOrders() {
        db.collection("orders")
                .whereEqualTo("status", "Not Delivered Yet")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderList.clear(); // Clear existing orders
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Order order = document.toObject(Order.class);
                            order.setId(document.getId()); // Set document ID for the order
                            orderList.add(order);
                        }
                        orderAdapter.notifyDataSetChanged(); // Update UI
                    } else {
                        Toast.makeText(DeliveryDashboard.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Fetch delivery history from Firestore
    private void loadDeliveryHistory() {
        db.collection("deliveries")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deliveryList.clear(); // Clear existing delivery history
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DeliveryRecord record = document.toObject(DeliveryRecord.class);
                            deliveryList.add(record);
                        }
                        deliveryHistoryAdapter.notifyDataSetChanged(); // Update UI
                    } else {
                        Toast.makeText(DeliveryDashboard.this, "Failed to load delivery history", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
