package com.example.project;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private TextView noPostsHint;

    public FeedFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noPostsHint = view.findViewById(R.id.noPostsHint); // Get the hint view

        postList = new ArrayList<>();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postAdapter = new PostAdapter(postList, currentUserId);
        recyclerView.setAdapter(postAdapter);

        db = FirebaseFirestore.getInstance();

        FloatingActionButton addPostFab = view.findViewById(R.id.addPostFab);
        addPostFab.setOnClickListener(v -> showCreatePostDialog());

        loadPosts(); // Load only posts from followed users

        return view;
    }

    private void loadPosts() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> followingList = (List<String>) documentSnapshot.get("following");

                    if (followingList == null || followingList.isEmpty()) {
                        postList.clear();
                        postAdapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.GONE);
                        noPostsHint.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "You're not following anyone yet.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!followingList.contains(currentUserId)) {
                        followingList.add(currentUserId); // Include self-posts
                    }

                    db.collection("posts")
                            .orderBy("timestamp")
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                postList.clear();
                                for (QueryDocumentSnapshot doc : querySnapshot) {
                                    String postUserId = doc.getString("userId");

                                    if (postUserId != null && followingList.contains(postUserId)) {
                                        Post post = doc.toObject(Post.class);
                                        post.setDocumentId(doc.getId()); // Store the Firestore doc ID
                                        postList.add(post);
                                    }
                                }

                                postAdapter.notifyDataSetChanged();

                                // 🔍 Show/hide "No posts" hint
                                if (postList.isEmpty()) {
                                    noPostsHint.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    noPostsHint.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error loading posts", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load following list", Toast.LENGTH_SHORT).show();
                });
    }

    private void showCreatePostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_post, null);
        EditText postContentEditText = dialogView.findViewById(R.id.postContentEditText);

        builder.setView(dialogView)
                .setTitle("Create Post")
                .setPositiveButton("Post", (dialog, which) -> {
                    String content = postContentEditText.getText().toString().trim();
                    if (!content.isEmpty()) {
                        savePost(content);
                    } else {
                        Toast.makeText(getContext(), "Post content cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void savePost(String content) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String username = documentSnapshot.getString("username");
                    long timestamp = System.currentTimeMillis();

                    Post post = new Post();
                    post.setUserId(userId);
                    post.setUsername(username != null ? username : "Unknown");
                    post.setContent(content);
                    post.setTimestamp(timestamp);

                    db.collection("posts")
                            .add(post)
                            .addOnSuccessListener(docRef -> {
                                Toast.makeText(getContext(), "Post added", Toast.LENGTH_SHORT).show();
                                loadPosts();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to post", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching user info", Toast.LENGTH_SHORT).show();
                });
    }
}

