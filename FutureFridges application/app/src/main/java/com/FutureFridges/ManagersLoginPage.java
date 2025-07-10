package com.FutureFridges;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.FutureFridges.MainPages.ManagersMainPage;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManagersLoginPage extends AppCompatActivity {
    private EditText usernameInput, passwordInput;
    private Button loginButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managers_login_page);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Handle login
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("employees")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        } else {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String storedPassword = document.getString("password");
                            String storedRole = document.getString("role");

                            if (storedPassword != null && storedPassword.equals(password)) {
                                if ("Manager".equals(storedRole)) {
                                    Toast.makeText(this, "Login successful as Manager", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ManagersLoginPage.this, ManagersMainPage.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, "This account is not for a Manager", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error logging in: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
