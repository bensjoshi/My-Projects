package com.FutureFridges;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManageUserAccess extends AppCompatActivity {

    private Button viewEmployeesButton;
    private Button signUpEmployeeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_user_access);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons
        viewEmployeesButton = findViewById(R.id.view_employees_button);
        signUpEmployeeButton = findViewById(R.id.sign_up_employee_button);

        // Set click listeners
        viewEmployeesButton.setOnClickListener(v -> {
            // Navigate to ViewEmployees activity (to be implemented)
            Intent intent = new Intent(ManageUserAccess.this, ViewEmployeesActivity.class);
            startActivity(intent);
        });

        signUpEmployeeButton.setOnClickListener(v -> {
            // Navigate to SignUpEmployee activity (to be implemented)
            Intent intent = new Intent(ManageUserAccess.this, SignUpEmployeeActivity.class);
            startActivity(intent);
        });
    }
}
