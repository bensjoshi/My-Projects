package com.FutureFridges;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_notifications, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(getActivity(), notificationList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadNotifications();
        return view;
    }

    void loadNotifications() {
        db.collection("notifications")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        notificationList.clear();
                        notificationList.addAll(queryDocumentSnapshots.toObjects(Notification.class));
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "No notifications available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkFoodExpiryAndAddNotifications() {
        long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000;
        long currentTimeMillis = System.currentTimeMillis();

        db.collection("food")
                .whereGreaterThan("expiryDate", new Timestamp(new java.util.Date(currentTimeMillis)))
                .whereLessThan("expiryDate", new Timestamp(new java.util.Date(currentTimeMillis + threeDaysInMillis)))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Item foodItem = document.toObject(Item.class);
                            if (foodItem != null) {
                                addNotification(foodItem);
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "No food items expiring soon", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to check food data", Toast.LENGTH_SHORT).show();
                });
    }

    private void addNotification(Item foodItem) {
        // 🔹 Ensure expiryDate is cast properly
        Timestamp expiryTimestamp = null;
        if (foodItem.getExpiryDate() instanceof Timestamp) {
            expiryTimestamp = (Timestamp) foodItem.getExpiryDate();
        }
        String expiryDateText = (expiryTimestamp != null) ? expiryTimestamp.toDate().toString() : "Unknown expiry date";
        String notificationText = "Expiry alert for " + foodItem.getFoodName() +
                ": The expiry date is approaching on " + expiryDateText;
        Notification notification = new Notification(Timestamp.now(), notificationText);
        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Notification added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to add notification", Toast.LENGTH_SHORT).show();
                });
    }
}