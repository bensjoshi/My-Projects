package com.FutureFridges;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class GeneralDashboardPage extends AppCompatActivity {
    private NotificationFragment notificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_dashboard);

        if (savedInstanceState == null) {
            notificationFragment = new NotificationFragment();
            InventoryFragment inventoryFragment = new InventoryFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.inventory_container, inventoryFragment);
            transaction.replace(R.id.notifications_container, notificationFragment);
            transaction.commit();
        } else {

            notificationFragment = (NotificationFragment) getSupportFragmentManager().findFragmentById(R.id.notifications_container);
        }
    }

    public void refreshNotifications() {
        if (notificationFragment != null) {
            notificationFragment.loadNotifications();
        }
    }
}
