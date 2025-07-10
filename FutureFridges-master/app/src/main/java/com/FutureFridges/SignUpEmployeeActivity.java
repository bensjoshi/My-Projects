package com.FutureFridges;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpEmployeeActivity extends AppCompatActivity {

    private Button signUpHeadChefButton;
    private Button signUpRegularChefButton;
    private Button signUpDeliveryPersonButton;
    private Button signUpManagerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_employee);

        // Initialize buttons
        signUpHeadChefButton = findViewById(R.id.sign_up_head_chef_button);
        signUpRegularChefButton = findViewById(R.id.sign_up_regular_chef_button);
        signUpDeliveryPersonButton = findViewById(R.id.sign_up_delivery_person_button);
        signUpManagerButton = findViewById(R.id.sign_up_manager_button);

        // Navigate to respective sign-up pages
        signUpHeadChefButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpEmployeeActivity.this, HeadChefSignUpPage.class);
            startActivity(intent);
        });

        signUpRegularChefButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpEmployeeActivity.this, RegularChefSignUpPage.class);
            startActivity(intent);
        });

        signUpDeliveryPersonButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpEmployeeActivity.this, DeliveryPersonSignUpPage.class);
            startActivity(intent);
        });

        signUpManagerButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpEmployeeActivity.this, ManagersSignUpPage.class);
            startActivity(intent);
        });
    }
}
