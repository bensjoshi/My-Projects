package com.example.project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView resultsRecyclerView;
    private ProgressBar loadingSpinner;
    private FirebaseFirestore db;
    private SearchAdapter adapter;
    private List<UserProfile> searchResults = new ArrayList<>();
    private List<String> userIds = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        searchEditText = view.findViewById(R.id.searchEditText);
        resultsRecyclerView = view.findViewById(R.id.resultsRecyclerView);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);

        adapter = new SearchAdapter(searchResults, userIds, (user, userId) -> {
            ViewProfileFragment fragment = ViewProfileFragment.newInstance(userId);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsRecyclerView.setAdapter(adapter);

        // Add listener for search input
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    searchResults.clear();
                    userIds.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }
//Performs the Firestore search query against user profiles.
    private void performSearch(String query) {
        loadingSpinner.setVisibility(View.VISIBLE);
        db.collection("users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    searchResults.clear();
                    userIds.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        UserProfile user = doc.toObject(UserProfile.class);
                        if (matchesQuery(user, query)) {
                            searchResults.add(user);
                            userIds.add(doc.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                    loadingSpinner.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> loadingSpinner.setVisibility(View.GONE));
    }
    //Checks if a user's profile fields match the given query.
    private boolean matchesQuery(UserProfile user, String query) {
        query = query.toLowerCase();
        return (user.getUsername() != null && user.getUsername().toLowerCase().contains(query)) ||
                (user.getLocation() != null && user.getLocation().toLowerCase().contains(query)) ||
                (user.getBio() != null && user.getBio().toLowerCase().contains(query)) ||
                (user.getInstruments() != null && user.getInstruments().toString().toLowerCase().contains(query)) ||
                (user.getGenres() != null && user.getGenres().toString().toLowerCase().contains(query));
    }
}
