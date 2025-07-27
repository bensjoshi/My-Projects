package com.example.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailEditText, nameEditText, instrumentEditText, locationEditText, bioEditText, genresEditText, passwordEditText, socialLinksEditText;
    ;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private Button selectProfilePicButton;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI elements
        emailEditText = findViewById(R.id.emailEditText);
        nameEditText = findViewById(R.id.nameEditText);
        instrumentEditText = findViewById(R.id.instrumentEditText);
        locationEditText = findViewById(R.id.locationEditText);
        bioEditText = findViewById(R.id.bioEditText);
        genresEditText = findViewById(R.id.genresEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        profileImageView = findViewById(R.id.profileImageView);
        selectProfilePicButton = findViewById(R.id.selectProfilePicButton);
        socialLinksEditText = findViewById(R.id.socialLinksEditText);

        // Back to login
        TextView backToLoginTextView = findViewById(R.id.backToLoginTextView);
        backToLoginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Select profile picture
        selectProfilePicButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Register user
        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String instrument = instrumentEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();
            String bio = bioEditText.getText().toString().trim();
            String genres = genresEditText.getText().toString().trim();
            String socialLinksInput = socialLinksEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)
                    || TextUtils.isEmpty(instrument) || TextUtils.isEmpty(location)
                    || TextUtils.isEmpty(bio) || TextUtils.isEmpty(genres)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(email, password, name, instrument, location, bio, genres, socialLinksInput);

        });
    }

    // Handle image picking result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);
        }
    }

    // Save image locally and get path
    private String saveImageLocally(Uri imageUri) {
        String savedImagePath = null;
        if (imageUri != null) {
            try {
                // Get the bitmap from the selected URI
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                // Define a file where the image will be saved locally
                File file = new File(getFilesDir(), "profile_" + System.currentTimeMillis() + ".jpg");

                // Save the bitmap as JPEG file in the app's internal storage
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();

                savedImagePath = file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save profile image", Toast.LENGTH_SHORT).show();
            }
        }
        return savedImagePath;
    }

    private void registerUser(String email, String password, String name, String instrumentInput, String location, String bio, String genresInput, String socialLinksInput) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            List<String> instruments = Arrays.asList(instrumentInput.split("\\s*,\\s*"));
                            List<String> genres = Arrays.asList(genresInput.split("\\s*,\\s*"));
                            List<String> socialLinks = Arrays.asList(socialLinksInput.split("\\s*,\\s*"));
                            List<String> emptyList = new ArrayList<>();

                            String savedImagePath = saveImageLocally(selectedImageUri);

                            UserProfile userProfile = new UserProfile(
                                    name,
                                    instruments,
                                    location,
                                    bio,
                                    genres,
                                    socialLinks,
                                    0,
                                    0,
                                    emptyList,
                                    emptyList,
                                    savedImagePath
                            );

                            db.collection("users").document(user.getUid())
                                    .set(userProfile)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirestoreError", "Error saving profile: " + e.getMessage());
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Log.e("AuthError", "Authentication failed: " + task.getException().getMessage());
                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


