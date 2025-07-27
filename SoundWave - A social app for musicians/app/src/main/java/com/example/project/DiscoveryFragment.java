package com.example.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DiscoveryFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView nameTextView;
    private TextView bioTextView;
    private TextView locationTextView;
    private TextView instrumentTextView;
    private TextView genresTextView;
    private Button followButton;
    private Button refreshMusicianButton;
    private ImageView profileImageView;

    private Button viewProfileButton;


    // Declare a variable to track the last selected user's ID
    private String lastSelectedUserId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        // Initialize Firestore and views
        db = FirebaseFirestore.getInstance();
        nameTextView = view.findViewById(R.id.name);
        bioTextView = view.findViewById(R.id.bio);
        locationTextView = view.findViewById(R.id.location);
        instrumentTextView = view.findViewById(R.id.instrument);
        genresTextView = view.findViewById(R.id.genres);
        followButton = view.findViewById(R.id.followButton);
        refreshMusicianButton = view.findViewById(R.id.refreshMusicianButton);
        profileImageView = view.findViewById(R.id.profileImage);
        viewProfileButton = view.findViewById(R.id.viewProfileButton);


        // Load one random musician initially
        fetchRandomMusicianProfile();

        viewProfileButton.setOnClickListener(v -> {
                    String selectedUserId = lastSelectedUserId;  // Get the ID of the selected user
                    if (selectedUserId != null && !selectedUserId.isEmpty()) {
                        // Navigate to the ViewProfileFragment
                        ViewProfileFragment viewProfileFragment = ViewProfileFragment.newInstance(selectedUserId);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, viewProfileFragment)  // Ensure the container ID is correct
                                .addToBackStack(null)  // Add to back stack to allow back navigation
                                .commit();
                    }
        });
        // Refresh button
        refreshMusicianButton.setOnClickListener(v -> fetchRandomMusicianProfile());

        return view;
    }

    private void fetchRandomMusicianProfile() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            List<DocumentSnapshot> usersList = querySnapshot.getDocuments();
                            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DocumentSnapshot randomUserDoc = null;
                            UserProfile user = null;

                            // Filter the list of users to exclude the current user
                            List<DocumentSnapshot> filteredUsersList = usersList.stream()
                                    .filter(doc -> !doc.getId().equals(currentUserId)) // exclude current user
                                    .filter(doc -> !doc.getId().equals(lastSelectedUserId)) // exclude last selected user
                                    .collect(Collectors.toList());

                            if (!filteredUsersList.isEmpty()) {
                                // Ensures that the randomly selected user is not the same as the last one
                                randomUserDoc = filteredUsersList.get(new Random().nextInt(filteredUsersList.size()));
                                user = randomUserDoc.toObject(UserProfile.class);

                                // Set the last selected user id
                                lastSelectedUserId = randomUserDoc.getId();

                                if (user != null) {
                                    // Joins instrument and genre lists as comma-separated strings
                                    String instruments = (user.getInstruments() != null)
                                            ? String.join(", ", user.getInstruments())
                                            : "N/A";

                                    String genres = (user.getGenres() != null)
                                            ? String.join(", ", user.getGenres())
                                            : "N/A";

                                    nameTextView.setText(user.getUsername());
                                    bioTextView.setText(user.getBio());
                                    locationTextView.setText(user.getLocation());
                                    instrumentTextView.setText(instruments);
                                    genresTextView.setText(genres);

                                    // Load the profile image from the local file path
                                    String profileImagePath = user.getProfileImagePath();
                                    if (profileImagePath != null && !profileImagePath.isEmpty()) {
                                        File imgFile = new File(profileImagePath);
                                        if (imgFile.exists()) {
                                            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                            profileImageView.setImageBitmap(bitmap);
                                        }
                                    } else {
                                        profileImageView.setImageResource(R.drawable.ic_profile);
                                    }

                                    // Store the selectedUserId outside the lambda to make it effectively final
                                    final String selectedUserId = randomUserDoc.getId(); // user being displayed
                                    DocumentReference currentUserRef = db.collection("users").document(currentUserId);
                                    currentUserRef.get().addOnSuccessListener(currentUserSnapshot -> {
                                        List<String> followingList = (List<String>) currentUserSnapshot.get("following");
                                        boolean isFollowing = followingList != null && followingList.contains(selectedUserId);
                                        followButton.setText(isFollowing ? "Unfollow" : "Follow");
                                    });
                                    followButton.setOnClickListener(v -> {
                                        if (!currentUserId.equals(selectedUserId)) {
                                            DocumentReference selectedUserRef = db.collection("users").document(selectedUserId);

                                            db.runTransaction(transaction -> {
                                                DocumentSnapshot currentUserDoc = transaction.get(currentUserRef);
                                                DocumentSnapshot selectedUserDoc = transaction.get(selectedUserRef);

                                                List<String> followingList = (List<String>) currentUserDoc.get("following");
                                                boolean isFollowing = followingList != null && followingList.contains(selectedUserId);

                                                if (!isFollowing) {
                                                    transaction.update(currentUserRef, "following", FieldValue.arrayUnion(selectedUserId));
                                                    transaction.update(selectedUserRef, "followers", FieldValue.arrayUnion(currentUserId));
                                                    transaction.update(currentUserRef, "followingCount", FieldValue.increment(1));
                                                    transaction.update(selectedUserRef, "followersCount", FieldValue.increment(1));
                                                    return "Followed";
                                                } else {
                                                    transaction.update(currentUserRef, "following", FieldValue.arrayRemove(selectedUserId));
                                                    transaction.update(selectedUserRef, "followers", FieldValue.arrayRemove(currentUserId));
                                                    transaction.update(currentUserRef, "followingCount", FieldValue.increment(-1));
                                                    transaction.update(selectedUserRef, "followersCount", FieldValue.increment(-1));
                                                    return "Unfollowed";
                                                }
                                            }).addOnSuccessListener(message -> {
                                                // Update button text based on action
                                                if ("Followed".equals(message)) {
                                                    followButton.setText("Unfollow");
                                                } else {
                                                    followButton.setText("Follow");
                                                }
                                            }).addOnFailureListener(e -> {
                                                Log.e("DiscoveryFragment", "Error updating follow status", e);
                                            });

                                        }
                                    });

                                }
                            } else {
                                Log.w("DashboardFragment", "No available musicians to display.");
                            }
                        } else {
                            Log.w("DashboardFragment", "No musicians found.");
                        }
                    } else {
                        Log.e("DashboardFragment", "Error fetching musicians", task.getException());
                    }
                });
    }
}
