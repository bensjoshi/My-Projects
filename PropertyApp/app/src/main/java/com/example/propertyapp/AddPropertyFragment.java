package com.example.propertyapp;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddPropertyFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for picking an image

    // Declare EditText views for all the fields in the UI
    private EditText etDescription, etAddress, etTenantName, etContactNo, etPostcode, etContactEmail, etDepositAmount, etRentPerMonth, etStartDate, etEndDate, etExtraInfo;
    private ImageView propertyImageView; // ImageView for displaying selected image
    private Uri selectedImageUri; // URI to store the selected image
    private String imagePath; // Path where the image will be saved

    public AddPropertyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout to display the UI
        View view = inflater.inflate(R.layout.fragment_add_property, container, false);

        // Initialize the EditText fields and ImageView
        etDescription = view.findViewById(R.id.etDescription);
        etAddress = view.findViewById(R.id.etAddress);
        etPostcode = view.findViewById(R.id.etPostcode);
        etTenantName = view.findViewById(R.id.etTenantName);
        etContactNo = view.findViewById(R.id.etContactNo);
        etContactEmail = view.findViewById(R.id.etContactEmail);
        etDepositAmount = view.findViewById(R.id.etDepositAmount);
        etRentPerMonth = view.findViewById(R.id.etRentPerMonth);
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        etExtraInfo = view.findViewById(R.id.etExtraInfo);
        propertyImageView = view.findViewById(R.id.PropertyImageView);

        // Set up button to allow image upload
        Button uploadImageButton = view.findViewById(R.id.btnUploadImage);
        uploadImageButton.setOnClickListener(v -> openFileChooser());

        // Get the current user's Firebase user ID
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser != null ? currentUser.getUid() : ""; // If no user is logged in, the ID will be empty

        // Set up the submit button that will add the property to Firestore
        view.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            // Retrieve input values from the EditText fields
            String description = etDescription.getText().toString();
            String address = etAddress.getText().toString();
            String postcode = etPostcode.getText().toString();
            String tenantName = etTenantName.getText().toString();
            String contactNo = etContactNo.getText().toString();
            String contactEmail = etContactEmail.getText().toString();
            String depositAmountStr = etDepositAmount.getText().toString();
            String rentPerMonthStr = etRentPerMonth.getText().toString();
            String startDateStr = etStartDate.getText().toString();
            String endDateStr = etEndDate.getText().toString();
            String extraInfo = etExtraInfo.getText().toString();

            // Validate that all fields have been filled in
            if (TextUtils.isEmpty(description) || TextUtils.isEmpty(address) || TextUtils.isEmpty(postcode) ||
                    TextUtils.isEmpty(tenantName) || TextUtils.isEmpty(contactNo) ||
                    TextUtils.isEmpty(contactEmail) || TextUtils.isEmpty(depositAmountStr) || TextUtils.isEmpty(rentPerMonthStr) ||
                    TextUtils.isEmpty(startDateStr) || TextUtils.isEmpty(endDateStr)) {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                // Create a Property object with the data from the form
                Property property = new Property(
                        description,        // description
                        address,            // address
                        postcode,           // postcode
                        tenantName,         // tenantName
                        contactNo,          // contactNo
                        contactEmail,       // contactEmail
                        depositAmountStr,   // depositAmount
                        rentPerMonthStr,    // rentPerMonth
                        startDateStr,       // startDate
                        endDateStr,         // endDate
                        extraInfo,          // extraInfo
                        userID,             // userID (from Firebase)
                        "",                 // documentID (empty initially)
                        imagePath           // imagePath (from selected image)
                );

                // Log the property object to debug
                Log.d("AddProperty", "Property userID: " + property.getUserID());

                // Reference Firestore database
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference propertiesRef = db.collection("properties");

                // Add the property to Firestore and handle success/failure
                propertiesRef.add(property)
                        .addOnSuccessListener(documentReference -> {
                            // Get the document ID after it is added to Firestore
                            String documentID = documentReference.getId();

                            // Set the document ID in the property object
                            property.setDocumentID(documentID);

                            // Log success
                            Log.d("AddProperty", "Property added with documentID: " + documentID);

                            // Notify the user of success
                            Toast.makeText(getContext(), "Property added successfully!", Toast.LENGTH_SHORT).show();

                            // Reset fields to prepare for adding another property
                            resetFields();
                        })
                        .addOnFailureListener(e -> {
                            // Notify the user if the property could not be added
                            Toast.makeText(getContext(), "Failed to add property: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        return view; // Return the fragment's view
    }

    // Helper method to open the file chooser for selecting an image
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Only allow image files
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Start activity to pick image
    }

    // Handle the result of the image picker
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData(); // Get the URI of the selected image
            try {
                // Set the selected image in the ImageView
                propertyImageView.setImageURI(selectedImageUri);

                // Save the image locally and get its path
                imagePath = saveImageToInternalStorage(selectedImageUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Save the selected image to internal storage and return its path
    private String saveImageToInternalStorage(Uri imageUri) throws IOException {
        File directory = getActivity().getFilesDir(); // Get the app's internal storage directory
        File file = new File(directory, "property_image_" + System.currentTimeMillis() + ".jpg");

        // Write the image to a file in internal storage
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // Compress and save
            outputStream.flush(); // Ensure the image is saved properly
        }

        return file.getAbsolutePath(); // Return the path of the saved image
    }

    // Helper method to reset the form fields after successful submission
    private void resetFields() {
        // Clear all EditText fields
        etDescription.setText("");
        etAddress.setText("");
        etTenantName.setText("");
        etContactNo.setText("");
        etPostcode.setText("");
        etContactEmail.setText("");
        etDepositAmount.setText("");
        etRentPerMonth.setText("");
        etStartDate.setText("");
        etEndDate.setText("");
        etExtraInfo.setText("");

        // Clear the image view
        propertyImageView.setImageDrawable(null);
    }
}
