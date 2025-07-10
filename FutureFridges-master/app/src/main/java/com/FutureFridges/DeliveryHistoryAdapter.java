package com.FutureFridges;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DeliveryHistoryAdapter extends RecyclerView.Adapter<DeliveryHistoryAdapter.ViewHolder> {
    private Context context;
    private List<DeliveryRecord> deliveryList;

    public DeliveryHistoryAdapter(Context context, List<DeliveryRecord> deliveryList) {
        this.context = context;
        this.deliveryList = deliveryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryRecord record = deliveryList.get(position);
        holder.foodNameTextView.setText(record.getFoodName());
        holder.quantityTextView.setText(String.valueOf(record.getQuantity()));

        // Format timestamp
        Timestamp timestamp = record.getTimestamp();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedDate = (timestamp != null) ? dateFormat.format(timestamp.toDate()) : "N/A";
        holder.timestampTextView.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView, quantityTextView, timestampTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}
