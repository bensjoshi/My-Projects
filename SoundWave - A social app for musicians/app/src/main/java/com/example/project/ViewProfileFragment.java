package com.example.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.List;

public class ViewProfileFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";
    private String userId;
    private String currentUserId;

    private TextView usernameTextView, bioTextView, locationTextView, instrumentsTextView,
            genresTextView, followersTextView, followingTextView;
    private LinearLayout socialLinksContainer;
    private ImageView profileImage;
    private Button followButton;

    private FirebaseFirestore db;

    public ViewProfileFragment() {}

    public static ViewProfileFragment newInstance(String userId) {
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        // Init views
        usernameTextView = view.findViewById(R.id.usernameTextView);
        bioTextView = view.findViewById(R.id.bioTextView);
        locationTextView = view.findViewById(R.id.locationTextView);
        instrumentsTextView = view.findViewById(R.id.instrumentsTextView);
        genresTextView = view.findViewById(R.id.genresTextView);
        followersTextView = view.findViewById(R.id.followersTextView);
        followingTextView = view.findViewById(R.id.followingTextView);
        profileImage = view.findViewById(R.id.profileImage);
        socialLinksContainer = view.findViewById(R.id.socialLinksContainer);
        followButton = view.findViewById(R.id.followButton);

        loadUserProfile();
        setupFollowButton();

        return view;
    }

    private void loadUserProfile() {
        if (userId == null) {
            Toast.makeText(getContext(), "User ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserProfile user = documentSnapshot.toObject(UserProfile.class);

                        if (user != null) {
                            usernameTextView.setText(user.getUsername());
                            bioTextView.setText(user.getBio());
                            locationTextView.setText(user.getLocation());

                            List<String> instruments = user.getInstruments();
                            instrumentsTextView.setText(instruments != null ? String.join(", ", instruments) : "N/A");

                            List<String> genres = user.getGenres();
                            genresTextView.setText(genres != null ? String.join(", ", genres) : "N/A");

                            followersTextView.setText("Followers: " + user.getFollowersCount());
                            followingTextView.setText("Following: " + user.getFollowingCount());

                            String profileImagePath = user.getProfileImagePath();
                            if (profileImagePath != null && !profileImagePath.isEmpty()) {
                                File imgFile = new File(profileImagePath);
                                if (imgFile.exists()) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                    profileImage.setImageBitmap(bitmap);
                                }
                            } else {
                                profileImage.setImageResource(R.drawable.ic_profile);
                            }

                            socialLinksContainer.removeAllViews();
                            List<String> socialLinks = user.getSocialLinks();
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
                            }
                        }

                    } else {
                        Toast.makeText(getContext(), "User profile not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error loading profile.", Toast.LENGTH_SHORT).show());
    }

    private void setupFollowButton() {
        if (userId.equals(currentUserId)) {
            followButton.setVisibility(View.GONE);
            return;
        }

        DocumentReference currentUserRef = db.collection("users").document(currentUserId);
        DocumentReference selectedUserRef = db.collection("users").document(userId);

        // Check initial state
        currentUserRef.get().addOnSuccessListener(currentUserSnapshot -> {
            List<String> followingList = (List<String>) currentUserSnapshot.get("following");
            boolean isFollowing = followingList != null && followingList.contains(userId);
            followButton.setText(isFollowing ? "Unfollow" : "Follow");
        });

        followButton.setOnClickListener(v -> {
            db.runTransaction(transaction -> {
                DocumentSnapshot currentUserDoc = transaction.get(currentUserRef);
                DocumentSnapshot selectedUserDoc = transaction.get(selectedUserRef);

                List<String> followingList = (List<String>) currentUserDoc.get("following");
                boolean isFollowing = followingList != null && followingList.contains(userId);

                if (!isFollowing) {
                    transaction.update(currentUserRef, "following", FieldValue.arrayUnion(userId));
                    transaction.update(selectedUserRef, "followers", FieldValue.arrayUnion(currentUserId));
                    transaction.update(currentUserRef, "followingCount", FieldValue.increment(1));
                    transaction.update(selectedUserRef, "followersCount", FieldValue.increment(1));
                    return "Followed";
                } else {
                    transaction.update(currentUserRef, "following", FieldValue.arrayRemove(userId));
                    transaction.update(selectedUserRef, "followers", FieldValue.arrayRemove(currentUserId));
                    transaction.update(currentUserRef, "followingCount", FieldValue.increment(-1));
                    transaction.update(selectedUserRef, "followersCount", FieldValue.increment(-1));
                    return "Unfollowed";
                }
            }).addOnSuccessListener(result -> {
                followButton.setText(result.equals("Followed") ? "Unfollow" : "Follow");
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error updating follow status.", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
