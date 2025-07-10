package com.FutureFridges;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DeliverOrdersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_orders);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        recyclerView.setAdapter(orderAdapter);

        db = FirebaseFirestore.getInstance();

        loadOrders();
    }

    private void loadOrders() {
        db.collection("orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderList.clear();
                        List<Order> notDeliveredOrders = new ArrayList<>();
                        List<Order> deliveredOrders = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Order order = document.toObject(Order.class);
                            order.setId(document.getId());
                            if ("Not Delivered Yet".equals(order.getStatus())) {
                                notDeliveredOrders.add(order);
                            } else {
                                deliveredOrders.add(order);
                            }
                        }
                        orderList.addAll(notDeliveredOrders);
                        orderList.addAll(deliveredOrders);

                        orderAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(DeliverOrdersActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}

