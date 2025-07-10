package com.FutureFridges;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private Context context;
    private List<Employee> employeeList;

    public EmployeeAdapter(Context context, List<Employee> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);

        // Bind employee data
        holder.usernameTextView.setText("Username: " + employee.getUsername());
        holder.passwordTextView.setText("Password: " + employee.getPassword());
        holder.roleTextView.setText("Role: " + employee.getRole());

        // Handle remove button click
        holder.removeButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("employees").document(employee.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Access removed for " + employee.getUsername(), Toast.LENGTH_SHORT).show();
                        employeeList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, employeeList.size());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to remove access", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, passwordTextView, roleTextView;
        Button removeButton;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            passwordTextView = itemView.findViewById(R.id.passwordTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
