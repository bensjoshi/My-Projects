package com.FutureFridges;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewHealthAndSafetyReports extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private List<Report> reportList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_health_and_safety_reports);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewReports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize list and adapter
        reportList = new ArrayList<>();
        adapter = new ReportAdapter(this, reportList);
        recyclerView.setAdapter(adapter);

        // Load reports from Firestore
        loadReports();
    }

    private void loadReports() {
        db.collection("health_and_safety_reports")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        reportList.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Report report = document.toObject(Report.class);
                            report.setId(document.getId()); // Assign document ID
                            reportList.add(report);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "No reports found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load reports", Toast.LENGTH_SHORT).show();
                });
    }
}
