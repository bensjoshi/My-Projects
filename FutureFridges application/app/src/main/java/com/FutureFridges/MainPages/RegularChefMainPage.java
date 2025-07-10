package com.FutureFridges.MainPages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.FutureFridges.GeneralDashboardPage;
import com.FutureFridges.OrderFoodActivity;
import com.FutureFridges.R;

public class RegularChefMainPage extends AppCompatActivity {

    private Button generalDashboardButton;
    private Button orderFoodButton;
    private Button notificationsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regular_chef_main_page2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize and set up the generalDashboardButton
        generalDashboardButton = findViewById(R.id.general_dashboard_button);
        generalDashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegularChefMainPage.this, GeneralDashboardPage.class);
            startActivity(intent);
        });

        // Initialize and set up the order food button
        orderFoodButton = findViewById(R.id.order_food_button);
        orderFoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegularChefMainPage.this, OrderFoodActivity.class);
            startActivity(intent);
        });

    }
}
