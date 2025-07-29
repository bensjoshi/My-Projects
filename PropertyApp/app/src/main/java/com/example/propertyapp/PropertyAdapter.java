package com.example.propertyapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private List<Property> propertyList;
    private OnItemClickListener listener;

    public PropertyAdapter(List<Property> propertyList, OnItemClickListener listener) {     //this property adapter used for adapt the objects into the recyclerview list
        this.propertyList = propertyList != null ? propertyList : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.property_item, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);

        // Set address and description
        holder.AddressTextView.setText(property.getAddress());
        holder.descriptionTextView.setText(property.getDescription());

        // Set the image for the property using BitmapFactory (for local file path)
        String imagePath = property.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.propertyImageView.setImageBitmap(bitmap); // Set the bitmap to the ImageView
            }
        } else {
            holder.propertyImageView.setImageResource(R.drawable.ic_default_picture); // Placeholder if no image
        }

        // Set the click listener on the item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(property); // Trigger the click event
            }
        });
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView AddressTextView, descriptionTextView;
        ImageView propertyImageView;

        public PropertyViewHolder(View itemView) {
            super(itemView);
            AddressTextView = itemView.findViewById(R.id.propertyAddress); // For address
            descriptionTextView = itemView.findViewById(R.id.propertyDescription); // For description
            propertyImageView = itemView.findViewById(R.id.propertyImageView); // For property image
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Property property);
    }
}
