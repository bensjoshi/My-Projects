package com.example.propertyapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private Button logoutButton;
    private Button uploadPictureButton;
    private ImageView profileImageView;

    private static final int PICK_IMAGE_REQUEST = 1;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userNameTextView = view.findViewById(R.id.userName);
        userEmailTextView = view.findViewById(R.id.userEmail);
        logoutButton = view.findViewById(R.id.logoutButton);
        uploadPictureButton = view.findViewById(R.id.uploadPictureButton);
        profileImageView = view.findViewById(R.id.profileImageView);

        // Get user details from Firebase
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            // Set the user data to the TextViews
            userNameTextView.setText(userName != null ? userName : "User Name");
            userEmailTextView.setText(userEmail != null ? userEmail : "user@example.com");
        }

        // Set a listener for the log out button
        logoutButton.setOnClickListener(v -> {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Start LoginActivity and clear the activity stack so the user cannot navigate back to the profile
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish(); // Close the current activity so the user can't go back to it
        });

        // Set a listener for the upload picture button
        uploadPictureButton.setOnClickListener(v -> {
            // Open a file chooser to select an image from local storage
            openFileChooser();
        });

        // Load the saved profile image (if any)
        loadProfileImage();

        return view;
    }

    private void openFileChooser() {
        // Create an intent to pick an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Convert the image URI into a bitmap and display it in the ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);

                // Save the image to internal storage and store the path in SharedPreferences
                saveImageToInternalStorage(bitmap);

                Toast.makeText(getActivity(), "Profile picture uploaded!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Save the image locally in the internal storage
    private void saveImageToInternalStorage(Bitmap bitmap) {
        try {
            // Create a file in internal storage
            File directory = getActivity().getFilesDir();
            File file = new File(directory, "profile_image.jpg");

            // Save the image to the file
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            // Save the file path in SharedPreferences
            saveImagePathToPreferences(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Failed to save image.", Toast.LENGTH_SHORT).show();
        }
    }

    // Save the image path in SharedPreferences
    private void saveImagePathToPreferences(String imagePath) {
        getActivity().getSharedPreferences("Profile", Context.MODE_PRIVATE)
                .edit()
                .putString("profile_image_path", imagePath)
                .apply();
    }

    // Load the saved profile image (if any)
    private void loadProfileImage() {
        // Retrieve the saved image path from SharedPreferences
        String imagePath = getActivity().getSharedPreferences("Profile", Context.MODE_PRIVATE)
                .getString("profile_image_path", null);

        if (imagePath != null) {
            // If the image path exists, load the image into ImageView
            File file = new File(imagePath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                profileImageView.setImageBitmap(bitmap);
            }
        }
    }
}
