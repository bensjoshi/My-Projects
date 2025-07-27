package com.example.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText usernameEditText, bioEditText, locationEditText, instrumentsEditText, genresEditText, socialLinksEditText;
    private ImageView profileImageView;
    private Button saveProfileButton, changeProfileImageButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Uri selectedImageUri;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind views
        usernameEditText = view.findViewById(R.id.edit_username);
        bioEditText = view.findViewById(R.id.edit_bio);
        locationEditText = view.findViewById(R.id.edit_location);
        instrumentsEditText = view.findViewById(R.id.edit_instruments);
        genresEditText = view.findViewById(R.id.edit_genres);
        socialLinksEditText = view.findViewById(R.id.edit_social_links);
        profileImageView = view.findViewById(R.id.edit_profile_image);
        saveProfileButton = view.findViewById(R.id.save_profile_button);
        changeProfileImageButton = view.findViewById(R.id.editProfileImageButton);

        // Load the current user's profile details
        loadUserProfile();

        // Handle the change profile image button click
        changeProfileImageButton.setOnClickListener(v -> openImageChooser());

        // Handle save button click
        saveProfileButton.setOnClickListener(v -> saveProfileChanges());

        return view;
    }

    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserProfile user = documentSnapshot.toObject(UserProfile.class);
                if (user != null) {
                    // Set the profile fields
                    usernameEditText.setText(user.getUsername());
                    bioEditText.setText(user.getBio());
                    locationEditText.setText(user.getLocation());
                    instrumentsEditText.setText(String.join(", ", user.getInstruments()));
                    genresEditText.setText(String.join(", ", user.getGenres()));

                    // Load the profile image if available
                    String profileImagePath = user.getProfileImagePath();
                    if (profileImagePath != null && !profileImagePath.isEmpty()) {
                        File imgFile = new File(profileImagePath);
                        if (imgFile.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            profileImageView.setImageBitmap(bitmap);
                        }
                    }

                    // Load social links if available
                    if (user.getSocialLinks() != null) {
                        socialLinksEditText.setText(String.join(", ", user.getSocialLinks()));
                    }
                }
            } else {
                Toast.makeText(getContext(), "User profile not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
            Log.e("EditProfileFragment", "Error fetching user profile", e);
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("EditProfileFragment", "Error selecting image", e);
            }
        }
    }

    private void saveProfileChanges() {
        String userId = mAuth.getCurrentUser().getUid();
        String username = usernameEditText.getText().toString().trim();
        String bio = bioEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String instruments = instrumentsEditText.getText().toString().trim();
        String genres = genresEditText.getText().toString().trim();
        String socialLinks = socialLinksEditText.getText().toString().trim();

        if (username.isEmpty() || bio.isEmpty() || location.isEmpty() || instruments.isEmpty() || genres.isEmpty()) {
            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> instrumentList = Arrays.asList(instruments.split("\\s*,\\s*"));
        List<String> genreList = Arrays.asList(genres.split("\\s*,\\s*"));
        List<String> socialLinkList = Arrays.asList(socialLinks.split("\\s*,\\s*"));  // Split lists

        DocumentReference userRef = db.collection("users").document(userId);

        // Prepare the base update map
        userRef.update(
                "username", username,
                "bio", bio,
                "location", location,
                "instruments", instrumentList,
                "genres", genreList,
                "socialLinks", socialLinkList  // Update social links in Firestore
        ).addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            Log.e("EditProfileFragment", "Error updating profile", e);
        });

        if (selectedImageUri != null) {
            try {
                // Get the bitmap from the selected URI
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);

                // Define a file where the image will be saved locally
                File file = new File(requireContext().getFilesDir(), "profile_" + System.currentTimeMillis() + ".jpg");

                // Save the bitmap as JPEG file
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();

                String savedImagePath = file.getAbsolutePath();

                // Update the Firestore document with the new profile image path
                userRef.update("profileImagePath", savedImagePath);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to save profile image", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
