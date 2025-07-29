package com.example.propertyapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class PropertyListFragment extends Fragment {

    private RecyclerView propertyRecyclerView;
    private PropertyAdapter propertyAdapter;
    private ArrayList<Property> propertyList;
    private FirebaseFirestore db;

    public PropertyListFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // Retain the fragment instance
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property_list, container, false);

        // Initialize RecyclerView and set layout manager
        propertyRecyclerView = view.findViewById(R.id.propertyRecyclerView);
        propertyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firestore and property list
        db = FirebaseFirestore.getInstance();
        propertyList = new ArrayList<>();

        // Load properties from Firestore
        loadProperties();

        return view;
    }

    private void loadProperties() {
        // Get current user ID
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Query Firestore for properties belonging to the current user
        db.collection("properties")
                .whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        // Iterate over the documents and create Property objects
                        for (var document : documentSnapshots) {
                            String documentID = document.getId();
                            String description = document.getString("description");
                            String address = document.getString("address");
                            String postcode = document.getString("postcode");
                            String tenantName = document.getString("tenantName");
                            String contactNo = document.getString("contactNo");
                            String contactEmail = document.getString("contactEmail");
                            String depositAmount = document.getString("depositAmount");
                            String rentPerMonth = document.getString("rentPerMonth");
                            String startDate = document.getString("startDate");
                            String endDate = document.getString("endDate");
                            String extraInfo = document.getString("extraInfo");
                            String imageUrl = document.getString("imagePath");

                            // Add the property to the list
                            Property property = new Property(
                                    description, address, postcode, tenantName, contactNo,
                                    contactEmail, depositAmount, rentPerMonth, startDate,
                                    endDate, extraInfo, userID, documentID, imageUrl
                            );
                            propertyList.add(property);
                        }

                        // Set up the PropertyAdapter and attach it to the RecyclerView
                        propertyAdapter = new PropertyAdapter(propertyList, property -> {
                            // Open the PropertyDetailFragment when a property is clicked
                            openPropertyDetails(property);
                        });

                        // Set the adapter to the RecyclerView
                        propertyRecyclerView.setAdapter(propertyAdapter);
                    } else {
                        Toast.makeText(getContext(), "Error loading properties.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openPropertyDetails(Property property) {
        // Create a new PropertyDetailFragment instance and pass the property object
        PropertyDetailFragment propertyDetailFragment = PropertyDetailFragment.newInstance(property);

        // Replace the current fragment with PropertyDetailFragment
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, propertyDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
