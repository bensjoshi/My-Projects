package com.FutureFridges;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment implements ItemAdapter.RefreshListener {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_inventory, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getContext(), itemList, this);
        recyclerView.setAdapter(itemAdapter);

        db = FirebaseFirestore.getInstance();
        fetchDataFromFirestore();
        checkFoodExpiryAndAddNotifications();

        return rootView;
    }

    private void fetchDataFromFirestore() {
        db.collection("food")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Item item = documentSnapshot.toObject(Item.class);
                            Object expiryObj = documentSnapshot.get("expiryDate");
                            item.setExpiryDate(expiryObj);

                            item.setFoodId(documentSnapshot.getId());
                            itemList.add(item);
                        }
                        itemAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No items available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching data", e);
                    Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDataChanged() {
        fetchDataFromFirestore();
    }

    private void checkFoodExpiryAndAddNotifications() {
        long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000;
        long currentTimeMillis = System.currentTimeMillis();

        db.collection("food")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Item foodItem = document.toObject(Item.class);
                            Timestamp expiryDate = foodItem.getExpiryTimestamp();
                            if (expiryDate != null) {
                                long expiryTimeMillis = expiryDate.toDate().getTime();
                                if (expiryTimeMillis > currentTimeMillis && expiryTimeMillis < currentTimeMillis + threeDaysInMillis) {
                                    addNotification(foodItem, "Expiry alert");
                                }
                            }

                            // 🔹 Low stock alert
                            if (foodItem.getQuantity() < 5) {
                                addNotification(foodItem, "Low quantity alert");
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "No food items found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch food data", Toast.LENGTH_SHORT).show();
                });
    }

    private void addNotification(Item foodItem, String alertType) {
        if (foodItem == null || foodItem.getFoodName() == null) {
            Log.e("NotificationError", "Invalid food item for notification");
            return;
        }

        String notificationText = "";

        if (alertType.equals("Expiry alert")) {
            notificationText = "Expiry alert for " + foodItem.getFoodName() +
                    ": The expiry date is approaching on " + foodItem.getFormattedExpiryDate();
        } else if (alertType.equals("Low quantity alert")) {
            notificationText = "Low quantity alert for " + foodItem.getFoodName() +
                    ": The quantity is less than 5. Current quantity: " + foodItem.getQuantity();
        }

        Notification notification = new Notification(Timestamp.now(), notificationText);
        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Notification", alertType + " added for " + foodItem.getFoodName());
                })
                .addOnFailureListener(e -> {
                    Log.e("NotificationError", "Failed to add notification", e);
                });
    }
}
