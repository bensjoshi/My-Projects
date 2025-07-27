package com.example.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView nameTextView, bioTextView, locationTextView, instrumentsTextView, genresTextView, followersTextView, followingTextView ;
    private ImageView profileImageView;
    private FirebaseAuth mAuth;
    private LinearLayout socialLinksContainer;
    private FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind views
        nameTextView = view.findViewById(R.id.profile_name);
        bioTextView = view.findViewById(R.id.profile_bio);
        locationTextView = view.findViewById(R.id.profile_location);
        instrumentsTextView = view.findViewById(R.id.profile_instruments);
        genresTextView = view.findViewById(R.id.profile_genres);
        followersTextView = view.findViewById(R.id.profile_followers);
        followingTextView = view.findViewById(R.id.profile_following);
        profileImageView = view.findViewById(R.id.profile_image);
        socialLinksContainer = view.findViewById(R.id.socialLinksContainer);


        // Load the user's profile
        loadUserProfile();

        // Initialize logout button
        Button logoutButton = view.findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(v -> {
            // Sign out the user from Firebase Authentication
            FirebaseAuth.getInstance().signOut();

            // Redirect to login activity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        // Initialize the Edit Profile button
        Button editProfileButton = view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> {
            // Navigate to the EditProfileFragment
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new EditProfileFragment());
            transaction.addToBackStack(null);  // Add this transaction to the back stack
            transaction.commit();
        });

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserProfile profile = documentSnapshot.toObject(UserProfile.class);
                        if (profile != null) {
                            nameTextView.setText(profile.getUsername());
                            bioTextView.setText(profile.getBio());
                            locationTextView.setText(profile.getLocation());

                            List<String> instruments = profile.getInstruments();
                            List<String> genres = profile.getGenres();

                            instrumentsTextView.setText(
                                    instruments != null ? String.join(", ", instruments) : "N/A");

                            genresTextView.setText(
                                    genres != null ? String.join(", ", genres) : "N/A");

                            List<String> socialLinks = profile.getSocialLinks();
                            socialLinksContainer.removeAllViews();

                            if (socialLinks != null && !socialLinks.isEmpty()) {
                                for (String link : socialLinks) {
                                    TextView linkView = new TextView(getContext());
                                    linkView.setText(link);
                                    linkView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                                    linkView.setPadding(0, 8, 0, 8);
                                    linkView.setClickable(true);
                                    linkView.setOnClickListener(v -> {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                        startActivity(browserIntent);
                                    });
                                    socialLinksContainer.addView(linkView);
                                }
                            } else {
                                TextView noLinks = new TextView(getContext());
                                noLinks.setText("No social links provided.");
                                noLinks.setPadding(0, 8, 0, 8);
                                socialLinksContainer.addView(noLinks);
                            }



                            followersTextView.setText(String.valueOf(profile.getFollowersCount()));
                            followingTextView.setText(String.valueOf(profile.getFollowingCount()));

                            // Load profile image
                            String profileImagePath = profile.getProfileImagePath();
                            if (profileImagePath != null && !profileImagePath.isEmpty()) {
                                // Load the image from the local path
                                File imgFile = new File(profileImagePath);
                                if (imgFile.exists()) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                    profileImageView.setImageBitmap(bitmap);
                                }
                            } else {
                                profileImageView.setImageResource(R.drawable.ic_profile);
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
                    Log.e("ProfileFragment", "Error fetching profile", e);
                });
    }
}
