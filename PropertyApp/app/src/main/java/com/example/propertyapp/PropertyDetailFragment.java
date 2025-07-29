package com.example.propertyapp;

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

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PropertyDetailFragment extends Fragment {

    private static final String ARG_DESCRIPTION = "description";        //string arguments for passing data
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_TENANT_NAME = "tenant_name";
    private static final String ARG_CONTACT_NUMBER = "contact_number";
    private static final String ARG_POSTCODE = "postcode";
    private static final String ARG_CONTACT_EMAIL = "contact_email";
    private static final String ARG_DEPOSIT_AMOUNT = "deposit_amount";
    private static final String ARG_RENT_PER_MONTH = "rent_per_month";
    private static final String ARG_START_DATE = "start_date";
    private static final String ARG_END_DATE = "end_date";
    private static final String ARG_EXTRA_INFO = "extra_info";
    private static final String ARG_DOCUMENT_ID = "documentID";
    private static final String ARG_IMAGE_PATH = "imagePath";

    private ImageView propertyImageView;

    public PropertyDetailFragment() {
        // Required empty public constructor
    }

    // New instance method to pass data to the fragment
    public static PropertyDetailFragment newInstance(Property property) {
        PropertyDetailFragment fragment = new PropertyDetailFragment();
        Bundle args = new Bundle();

        args.putString(ARG_DESCRIPTION, property.getDescription());     //matche the arguments with the property data
        args.putString(ARG_ADDRESS, property.getAddress());
        args.putString(ARG_TENANT_NAME, property.getTenantName());
        args.putString(ARG_CONTACT_NUMBER, property.getContactNo());
        args.putString(ARG_POSTCODE, property.getPostcode());
        args.putString(ARG_CONTACT_EMAIL, property.getContactEmail());
        args.putString(ARG_DEPOSIT_AMOUNT, property.getDepositAmount());
        args.putString(ARG_RENT_PER_MONTH, property.getRentPerMonth());
        args.putString(ARG_START_DATE, property.getStartDate());
        args.putString(ARG_END_DATE, property.getEndDate());
        args.putString(ARG_EXTRA_INFO, property.getExtraInfo());
        args.putString(ARG_DOCUMENT_ID, property.getDocumentID());
        args.putString(ARG_IMAGE_PATH, property.getImagePath());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property_detail, container, false);

        // Initialize the TextViews
        TextView descriptionTextView = view.findViewById(R.id.propertyDescription);
        TextView addressTextView = view.findViewById(R.id.propertyAddress);
        TextView tenantNameTextView = view.findViewById(R.id.tenantName);
        TextView contactNumberTextView = view.findViewById(R.id.contactNumber);
        TextView postcodeTextView = view.findViewById(R.id.postcode);
        TextView contactEmailTextView = view.findViewById(R.id.contactEmail); // TextView for contact email
        TextView depositAmountTextView = view.findViewById(R.id.depositAmount); // TextView for deposit amount
        TextView rentPerMonthTextView = view.findViewById(R.id.rentPerMonth); // TextView for rent per month
        TextView startDateTextView = view.findViewById(R.id.startDate); // TextView for start date
        TextView endDateTextView = view.findViewById(R.id.endDate); // TextView for tenancy end date
        TextView extraInfoTextView = view.findViewById(R.id.extraInfo); // TextView for extra info

        // Initialize ImageView
        propertyImageView = view.findViewById(R.id.PropertyImageView);

        // Set the data from the arguments to the corresponding TextViews
        if (getArguments() != null) {
            String description = getArguments().getString(ARG_DESCRIPTION);
            String address = getArguments().getString(ARG_ADDRESS);
            String tenantName = getArguments().getString(ARG_TENANT_NAME);
            String contactNumber = getArguments().getString(ARG_CONTACT_NUMBER);
            String postcode = getArguments().getString(ARG_POSTCODE);
            String contactEmail = getArguments().getString(ARG_CONTACT_EMAIL);
            String depositAmount = getArguments().getString(ARG_DEPOSIT_AMOUNT);
            String rentPerMonth = getArguments().getString(ARG_RENT_PER_MONTH);
            String startDate = getArguments().getString(ARG_START_DATE);
            String endDate = getArguments().getString(ARG_END_DATE);
            String extraInfo = getArguments().getString(ARG_EXTRA_INFO);
            String documentID = getArguments().getString(ARG_DOCUMENT_ID);
            String imagePath = getArguments().getString(ARG_IMAGE_PATH);

            // Set the text to the TextViews
            descriptionTextView.setText(description);
            addressTextView.setText(address);
            postcodeTextView.setText(postcode);
            tenantNameTextView.setText(tenantName);
            contactNumberTextView.setText(contactNumber);
            contactEmailTextView.setText(contactEmail);
            String depositAmountFormatted = (depositAmount != null && !depositAmount.isEmpty())
                    ? (depositAmount.startsWith("£") ? depositAmount : "£" + depositAmount)     //adds £ sign unless it has already been added previously
                    : "Deposit Amount: Not available";
            depositAmountTextView.setText(depositAmountFormatted);

            String rentPerMonthFormatted = (rentPerMonth != null && !rentPerMonth.isEmpty())
                    ? (rentPerMonth.startsWith("£") ? rentPerMonth : "£" + rentPerMonth)
                    : "Rent Per Month: Not available";
            rentPerMonthTextView.setText(rentPerMonthFormatted);

            rentPerMonthTextView.setText(rentPerMonthFormatted);
            startDateTextView.setText(startDate);
            endDateTextView.setText(endDate);
            extraInfoTextView.setText(extraInfo);

            // Load the image if the imagePath exists
            if (imagePath != null && !imagePath.isEmpty()) {
                loadImageFromStorage(imagePath);
            }

            // Handle the edit button click to navigate to the update property fragment
            Button editButton = view.findViewById(R.id.editButton);
            editButton.setOnClickListener(v -> {
                // Pass the property data to the UpdatePropertyFragment for editing, including the new fields
                UpdatePropertyFragment updatePropertyFragment = UpdatePropertyFragment.newInstance(
                        new Property(
                                descriptionTextView.getText().toString(),
                                addressTextView.getText().toString(),
                                postcodeTextView.getText().toString(),
                                tenantNameTextView.getText().toString(),
                                contactNumberTextView.getText().toString(),
                                contactEmailTextView.getText().toString(),
                                depositAmountTextView.getText().toString(),
                                rentPerMonthTextView.getText().toString(),
                                startDateTextView.getText().toString(),
                                endDateTextView.getText().toString(),
                                extraInfoTextView.getText().toString(),
                                "",
                                documentID,
                                imagePath
                        ));

                // Replace the current fragment with the update property fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, updatePropertyFragment)
                        .addToBackStack(null)
                        .commit();
            });

            // Handle the delete button click to delete the property from Firestore
            Button deleteButton = view.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(v -> {
                // Delete the property from Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("properties").document(documentID)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Property deleted successfully!", Toast.LENGTH_SHORT).show();
                            // Navigate back to the previous fragment
                            getActivity().getSupportFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to delete property: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        }

        return view;
    }

    private void loadImageFromStorage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(getContext(), "Invalid image path", Toast.LENGTH_SHORT).show();
            return;
        }

        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            try {
                // Decode the file to a Bitmap
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if (bitmap != null) {
                    propertyImageView.setImageBitmap(bitmap);  // Set the Bitmap in ImageView
                } else {
                    Toast.makeText(getContext(), "Failed to decode image.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // Log and show error message
                Log.e("ImageLoadError", "Error loading image: " + e.getMessage());
                Toast.makeText(getContext(), "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // File doesn't exist at the specified path
            Toast.makeText(getContext(), "Image not found at path: " + imagePath, Toast.LENGTH_SHORT).show();
        }
    }
}
