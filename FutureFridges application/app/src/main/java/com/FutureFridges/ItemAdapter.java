package com.FutureFridges;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private Context context;
    private List<Item> itemList;
    private FirebaseFirestore db;
    private RefreshListener refreshListener;

    public interface RefreshListener {
        void onDataChanged();
    }

    public ItemAdapter(Context context, List<Item> itemList, RefreshListener refreshListener) {
        this.context = context;
        this.itemList = itemList;
        this.db = FirebaseFirestore.getInstance();
        this.refreshListener = refreshListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.foodNameTextView.setText(item.getFoodName());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.expiryDateTextView.setText(item.getFormattedExpiryDate());

        holder.removeButton.setOnClickListener(v -> {
            deleteItemFromFirestore(item.getFoodId(), position,item.getFoodName());
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView foodNameTextView, quantityTextView, expiryDateTextView;
        Button removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            expiryDateTextView = itemView.findViewById(R.id.expiryDateTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    private void addItemRemovedNotification(String foodName, long timestampMillis) {
        Timestamp timestamp = new Timestamp(new java.util.Date(timestampMillis));
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("text", "Item " + foodName + " has been removed from the fridge.");
        notificationData.put("date", timestamp);

        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Notification", "Notification added successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Notification", "Failed to add notification", e);
                });
    }

    private void deleteItemFromFirestore(String foodId, int position, String foodName) {
        db.collection("food")
                .document(foodId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Item successfully deleted!");
                    removeItem(position);
                    addItemRemovedNotification(foodName, System.currentTimeMillis());

                    if (refreshListener != null) {
                        refreshListener.onDataChanged();
                    }
                    if (context instanceof GeneralDashboardPage) {
                        ((GeneralDashboardPage) context).refreshNotifications();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error deleting item", e);
                });
    }



    private void removeItem(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
    }
}