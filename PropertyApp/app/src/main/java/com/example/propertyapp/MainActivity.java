package com.example.propertyapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);//initialise bottom navigation

        // Set initial fragment (Home or any default fragment)
        loadFragment(new PropertyListFragment());

        // Set listener for item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                loadFragment(new PropertyListFragment());//home fragment
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                loadFragment(new ProfileFragment());//profile fragment
                return true;
            } else if (item.getItemId() == R.id.nav_add_property) {
                loadFragment(new AddPropertyFragment());  // Add Property Fragment
                return true;

            }
            return false;
        });
    }

    // Method to load fragments
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);  // fade transition
        transaction.commit();
    }
}
