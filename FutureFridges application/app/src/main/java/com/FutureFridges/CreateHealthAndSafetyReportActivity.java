package com.FutureFridges;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateHealthAndSafetyReportActivity extends AppCompatActivity {

    private EditText reportTitleInput, reportDescriptionInput, reportTypeInput, reportStatusInput;
    private Button submitButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_health_and_safety_report);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        reportTitleInput = findViewById(R.id.report_title_input);
        reportDescriptionInput = findViewById(R.id.report_description_input);
        reportTypeInput = findViewById(R.id.report_type_input);
        reportStatusInput = findViewById(R.id.report_status_input);
        submitButton = findViewById(R.id.submit_button);

        // Handle form submission
        submitButton.setOnClickListener(v -> submitReport());
    }

    private void submitReport() {
        String title = reportTitleInput.getText().toString().trim();
        String description = reportDescriptionInput.getText().toString().trim();
        String reportType = reportTypeInput.getText().toString().trim();
        String reportStatus = reportStatusInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || reportType.isEmpty() || reportStatus.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data for Firestore
        Map<String, Object> report = new HashMap<>();
        report.put("title", title);
        report.put("description", description);
        report.put("type", reportType);
        report.put("status", reportStatus);
        report.put("date", Timestamp.now());

        // Save to Firestore
        db.collection("health_and_safety_reports")
                .add(report)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit report", Toast.LENGTH_SHORT).show();
                });
    }
}
