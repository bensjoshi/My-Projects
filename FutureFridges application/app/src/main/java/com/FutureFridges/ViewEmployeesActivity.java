package com.FutureFridges;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewEmployeesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private List<Employee> employeeList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_employees);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        employeeList = new ArrayList<>();
        adapter = new EmployeeAdapter(this, employeeList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchEmployees();
    }

    private void fetchEmployees() {
        db.collection("employees")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    employeeList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Employee employee = document.toObject(Employee.class);
                        employee.setId(document.getId()); // Set document ID for deletion
                        employeeList.add(employee);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewEmployeesActivity.this, "Failed to fetch employees", Toast.LENGTH_SHORT).show();
                });
    }

    public void removeEmployee(Employee employee) {
        db.collection("employees").document(employee.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ViewEmployeesActivity.this, "Employee removed successfully", Toast.LENGTH_SHORT).show();
                    employeeList.remove(employee);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewEmployeesActivity.this, "Failed to remove employee", Toast.LENGTH_SHORT).show();
                });
    }
}