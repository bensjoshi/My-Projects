package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final List<UserProfile> users;
    private final List<String> userIds;
    private final OnUserClickListener clickListener;

    public interface OnUserClickListener {
        void onUserClick(UserProfile user, String userId);
    }

    public SearchAdapter(List<UserProfile> users, List<String> userIds, OnUserClickListener listener) {
        this.users = users;
        this.userIds = userIds;
        this.clickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, locationTextView;

        public ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.usernameTextView);
            locationTextView = view.findViewById(R.id.locationTextView);
        }
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_result, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        UserProfile user = users.get(position);
        String userId = userIds.get(position);

        holder.nameTextView.setText(user.getUsername());
        holder.locationTextView.setText(user.getLocation());

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onUserClick(user, userId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
