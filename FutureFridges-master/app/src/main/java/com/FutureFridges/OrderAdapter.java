package com.FutureFridges;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;
    private FirebaseFirestore db;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.foodNameTextView.setText(order.getFoodName());
        holder.quantityTextView.setText(String.valueOf(order.getQuantity()));
        holder.statusTextView.setText(order.getStatus());

        if ("Not Delivered Yet".equals(order.getStatus())) {
            holder.deliverButton.setVisibility(View.VISIBLE);
        } else {
            holder.deliverButton.setVisibility(View.GONE);
        }

        holder.deliverButton.setOnClickListener(v -> markAsDelivered(order, position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // ✅ Mark an order as delivered
    private void markAsDelivered(Order order, int position) {
        db.collection("orders")
                .document(order.getId())
                .update("status", "Delivered")
                .addOnSuccessListener(aVoid -> {
                    order.setStatus("Delivered");
                    notifyItemChanged(position);

                    // Step 1: Add the delivered food item to "food" collection
                    addFoodItem(order);

                    // Step 2: Create a delivery history record
                    Timestamp deliveryTimestamp = new Timestamp(new java.util.Date());
                    DeliveryRecord record = new DeliveryRecord(order.getFoodName(), order.getQuantity(), deliveryTimestamp);

                    db.collection("deliveries").add(record)
                            .addOnSuccessListener(documentReference ->
                                    Toast.makeText(context, "Order added to Delivery History", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Failed to add delivery history", Toast.LENGTH_SHORT).show());

                    // Notify driver
                    Toast.makeText(context, "Order marked as delivered", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }

    // ✅ Move delivered item to "food" collection
    private void addFoodItem(Order order) {
        Map<String, Object> foodData = new HashMap<>();
        foodData.put("foodName", order.getFoodName());
        foodData.put("quantity", order.getQuantity());

        // If expiry date is provided, use it; otherwise, default to 7 days from now
        if (order.getExpiryDate() instanceof Timestamp) {
            foodData.put("expiryDate", order.getExpiryDate());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            foodData.put("expiryDate", new Timestamp(calendar.getTime()));
        }

        db.collection("food").add(foodData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Item added to food collection!", Toast.LENGTH_SHORT).show();

                    // Step 3: Remove the order from "orders" since it has been delivered
                    removeOrderFromOrders(order.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to add item to food collection", Toast.LENGTH_SHORT).show();
                });
    }

    // ✅ Remove order from "orders" after successful delivery
    private void removeOrderFromOrders(String orderId) {
        db.collection("orders").document(orderId)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Order removed from orders", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to remove order", Toast.LENGTH_SHORT).show());
    }

    // ✅ ViewHolder Class
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView, quantityTextView, statusTextView;
        Button deliverButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            deliverButton = itemView.findViewById(R.id.deliverButton);
        }
    }
}
