package com.FutureFridges;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.FutureFridges.MainPages.HeadChefMainPages;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HeadChefLoginPage extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginButton;

    // Firestore reference
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_chef_login_page);

        // Initialize views
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton   = findViewById(R.id.login_button);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Login button -> validate the username & password in Firestore
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Check for empty fields
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Query Firestore for matching username
            db.collection("employees")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            // No user with that username
                            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        } else {
                            // We found at least one document for that username
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                            String storedPassword = document.getString("password");
                            String storedRole = document.getString("role");

                            if (storedPassword != null && storedPassword.equals(password)) {
                                // Correct password
                                Toast.makeText(this, "Login successful as " + storedRole, Toast.LENGTH_SHORT).show();

                                // Navigate to HeadChefMainPages
                                Intent intent = new Intent(HeadChefLoginPage.this, HeadChefMainPages.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Password mismatch
                                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // For example, a network error
                        Toast.makeText(this, "Error logging in: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
