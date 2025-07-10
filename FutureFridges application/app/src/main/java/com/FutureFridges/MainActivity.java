package com.FutureFridges;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the buttons by ID
        Button headChefButton = findViewById(R.id.head_chef_button);
        Button regularChefButton = findViewById(R.id.regular_chef_button);
        Button deliveryPersonButton = findViewById(R.id.delivery_person_button);
        Button managersButton = findViewById(R.id.managers_button);



        // Set listeners for button clicks
        headChefButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HeadChefLoginPage.class);
            startActivity(intent);
        });

        regularChefButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegularChefLoginPage.class);
            startActivity(intent);
        });

        deliveryPersonButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DeliveryPersonLoginPage.class);
            startActivity(intent);
        });

        managersButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManagersLoginPage.class);
            startActivity(intent);
        });

    }
}
