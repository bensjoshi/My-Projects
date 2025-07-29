package com.example.propertyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpdatePropertyFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etDescription, etAddress, etTenantName, etContactNo, etPostcode, etDepositAmount,
            etRentPerMonth, etStartDate, etEndDate, etExtraInfo, etContactEmail;
    private ImageView propertyImageView;
    private Button uploadImageButton, saveButton;

    private Uri selectedImageUri;
    private String imagePath;
    private String documentId;  // Document ID for the current property
    private String userID;

    private FirebaseFirestore db;

    public UpdatePropertyFragment() {
        // Required empty public constructor
    }
    public static UpdatePropertyFragment newInstance(Property property) {
        UpdatePropertyFragment fragment = new UpdatePropertyFragment();
        Bundle args = new Bundle();

        // Pass the relevant fields from the property
        args.putString("description", property.getDescription());
        args.putString("address", property.getAddress());
        args.putString("postcode", property.getPostcode());
        args.putString("tenantName", property.getTenantName());
        args.putString("contactNo", property.getContactNo());
        args.putString("contactEmail", property.getContactEmail());
        args.putString("depositAmount", property.getDepositAmount());
        args.putString("rentPerMonth", property.getRentPerMonth());
        args.putString("startDate", property.getStartDate());
        args.putString("endDate", property.getEndDate());
        args.putString("extraInfo", property.getExtraInfo());
        args.putString("userID", property.getUserID());
        args.putString("documentID", property.getDocumentID());
        args.putString("imagePath", property.getImagePath());

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_property, container, false);

        // Initialize views
        etDescription = view.findViewById(R.id.editDescription);
        etAddress = view.findViewById(R.id.editAddress);
        etPostcode = view.findViewById(R.id.editPostcode);
        etTenantName = view.findViewById(R.id.editTenantName);
        etContactNo = view.findViewById(R.id.editContactNumber);
        etContactEmail = view.findViewById(R.id.editContactEmail);
        etDepositAmount = view.findViewById(R.id.editDepositAmount);
        etRentPerMonth = view.findViewById(R.id.editRentPerMonth);
        etStartDate = view.findViewById(R.id.editStartDate);
        etEndDate = view.findViewById(R.id.editEndDate);
        etExtraInfo = view.findViewById(R.id.editExtraInfo);
        propertyImageView = view.findViewById(R.id.propertyImageView);

        uploadImageButton = view.findViewById(R.id.uploadImageButton);
        saveButton = view.findViewById(R.id.saveButton);

        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            etDescription.setText(getArguments().getString("description"));
            etAddress.setText(getArguments().getString("address"));
            etPostcode.setText(getArguments().getString("postcode"));
            etTenantName.setText(getArguments().getString("tenantName"));
            etContactNo.setText(getArguments().getString("contactNo"));
            etContactEmail.setText(getArguments().getString("contactEmail"));
            etDepositAmount.setText(getArguments().getString("depositAmount"));
            etRentPerMonth.setText(getArguments().getString("rentPerMonth"));
            etStartDate.setText(getArguments().getString("startDate"));
            etEndDate.setText(getArguments().getString("endDate"));
            etExtraInfo.setText(getArguments().getString("extraInfo"));
            userID = getArguments().getString("userID");
            documentId = getArguments().getString("documentID");
            imagePath = getArguments().getString("imagePath");

            // Load the existing image if available
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    propertyImageView.setImageURI(Uri.fromFile(imageFile));
                }
            }
        }


        // Set up the upload image button
        uploadImageButton.setOnClickListener(v -> openImagePicker());

        // Set up the save button
        saveButton.setOnClickListener(v -> savePropertyDetails());

        return view;
    }

    // Open the image picker to select an image
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image picker
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                // Display the selected image in ImageView
                propertyImageView.setImageURI(selectedImageUri);

                // Save the image locally and get the image path
                imagePath = saveImageToInternalStorage(selectedImageUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Save the image locally and return the image path
    private String saveImageToInternalStorage(Uri imageUri) throws IOException {
        File directory = getActivity().getFilesDir();
        File file = new File(directory, "property_image_" + System.currentTimeMillis() + ".jpg");

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
        }

        return file.getAbsolutePath(); // Return the saved image path
    }

    // Save the updated property details to Firestore
    private void savePropertyDetails() {
        String description = etDescription.getText().toString();
        String address = etAddress.getText().toString();
        String postcode = etPostcode.getText().toString();
        String tenantName = etTenantName.getText().toString();
        String contactNo = etContactNo.getText().toString();
        String contactEmail = etContactEmail.getText().toString();
        String depositAmount = etDepositAmount.getText().toString();
        String rentPerMonth = etRentPerMonth.getText().toString();
        String startDate = etStartDate.getText().toString();
        String endDate = etEndDate.getText().toString();
        String extraInfo = etExtraInfo.getText().toString();

        if (TextUtils.isEmpty(description) || TextUtils.isEmpty(address) ||TextUtils.isEmpty(postcode) || TextUtils.isEmpty(tenantName) ||
                TextUtils.isEmpty(contactNo) || TextUtils.isEmpty(contactEmail) ||
                TextUtils.isEmpty(depositAmount) || TextUtils.isEmpty(rentPerMonth) || TextUtils.isEmpty(startDate) ||
                TextUtils.isEmpty(endDate)) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        } else {
            // Create the Property object with updated information
            Property updatedProperty = new Property(description, address, postcode, tenantName, contactNo,
                    contactEmail, depositAmount, rentPerMonth, startDate, endDate, extraInfo, userID, documentId, imagePath);

            // Update the property in Firestore
            db.collection("properties").document(documentId)
                    .update(
                            "description", description,
                            "address", address,
                            "postcode", postcode,
                            "tenantName", tenantName,
                            "contactNo", contactNo,
                            "contactEmail", contactEmail,
                            "depositAmount", depositAmount,
                            "rentPerMonth", rentPerMonth,
                            "startDate", startDate,
                            "endDate", endDate,
                            "extraInfo", extraInfo,
                            "imagePath", imagePath
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Property details updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update property details", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
