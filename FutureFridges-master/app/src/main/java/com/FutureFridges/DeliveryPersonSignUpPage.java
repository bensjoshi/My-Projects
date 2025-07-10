package com.FutureFridges;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.FutureFridges.MainPages.DeliveryPersonMainPage;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeliveryPersonSignUpPage extends AppCompatActivity {
    private EditText usernameInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_person_sign_up_page);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.sign_up_button);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the username already exists in Firestore
        db.collection("employees")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add new user to Firestore with role = "Delivery Person"
                        DocumentReference newUserRef = db.collection("employees").document();
                        newUserRef.set(new User(username, password, "Delivery Person"))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "You successfully signed up as a Delivery Person.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(DeliveryPersonSignUpPage.this, DeliveryDashboard.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error checking username", Toast.LENGTH_SHORT).show());
    }

    // User class for Firestore document model
    public static class User {
        private String username;
        private String password;
        private String role;

        public User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }
    }
}
