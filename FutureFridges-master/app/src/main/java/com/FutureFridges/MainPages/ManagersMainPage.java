package com.FutureFridges.MainPages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.FutureFridges.CreateHealthAndSafetyReportActivity;
import com.FutureFridges.GeneralDashboardPage;
import com.FutureFridges.R;
import com.FutureFridges.ManageUserAccess;
import com.FutureFridges.ViewHealthAndSafetyReports;

public class ManagersMainPage extends AppCompatActivity {

    private Button generalDashboardButton;
    private Button viewReportsButton;
    private Button createReportButton;
    private Button manageUserAccessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_managers_main_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons
        generalDashboardButton = findViewById(R.id.general_dashboard_button);
        viewReportsButton = findViewById(R.id.view_reports_button);
        createReportButton = findViewById(R.id.create_report_button);
        manageUserAccessButton = findViewById(R.id.manage_user_access_button);

        // Navigate to Dashboard page
        generalDashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagersMainPage.this, GeneralDashboardPage.class);
            startActivity(intent);
        });

        // Navigate to ViewHealthAndSafetyReports page
        viewReportsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagersMainPage.this, ViewHealthAndSafetyReports.class);
            startActivity(intent);
        });

        // Navigate to CreateHealthAndSafetyReport page
        createReportButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagersMainPage.this, CreateHealthAndSafetyReportActivity.class);
            startActivity(intent);
        });

        // Navigate to ManageUserAccess page
        manageUserAccessButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagersMainPage.this, ManageUserAccess.class);
            startActivity(intent);
        });
    }
}
