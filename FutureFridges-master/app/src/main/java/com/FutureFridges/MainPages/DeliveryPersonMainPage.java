package com.FutureFridges.MainPages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.FutureFridges.DeliverOrdersActivity;
import com.FutureFridges.R;

public class DeliveryPersonMainPage extends AppCompatActivity {

    private Button deliverOrdersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery_person_main_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize and set up the Deliver Orders button
        deliverOrdersButton = findViewById(R.id.deliver_orders_button);
        deliverOrdersButton.setOnClickListener(v -> {
            Intent intent = new Intent(DeliveryPersonMainPage.this, DeliverOrdersActivity.class);
            startActivity(intent);
        });
    }
}
