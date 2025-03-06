package com.example.mindease;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register_AdminActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText adminID, adminNameRes, adminAgeRes, adminGenderRes, adminEmailRes, adminUnameRes, adminPassRes;
    private Button adminButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_admin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://mindease-e0e70-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users");

        // Initialize UI elements
        adminID = findViewById(R.id.adminID);
        adminNameRes = findViewById(R.id.adminNameRes);
        adminAgeRes = findViewById(R.id.adminAgeRes);
        adminGenderRes = findViewById(R.id.adminGenderRes);
        adminEmailRes = findViewById(R.id.adminEmailRes);
        adminUnameRes = findViewById(R.id.adminUnameRes);
        adminPassRes = findViewById(R.id.adminPassRes);
        adminButt = findViewById(R.id.adminButt);

        // Make adminID read-only
        adminID.setEnabled(false);

        // Fetch and update the admin ID properly
        fetchAndIncrementAdminId();

        adminButt.setOnClickListener(v -> registerAdmin());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchAndIncrementAdminId() {
        mDatabase.child("adminIdCount").runTransaction(new com.google.firebase.database.Transaction.Handler() {
            @Override
            public com.google.firebase.database.Transaction.Result doTransaction(com.google.firebase.database.MutableData mutableData) {
                Integer currentId = mutableData.getValue(Integer.class);
                if (currentId == null) {
                    return com.google.firebase.database.Transaction.success(mutableData);
                }
                mutableData.setValue(currentId + 1); // Increment ID in Firebase
                return com.google.firebase.database.Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (committed && snapshot.exists()) {
                    int newAdminId = snapshot.getValue(Integer.class);
                    adminID.setText(String.valueOf(newAdminId)); // Set the new ID to text field
                } else {
                    Toast.makeText(Register_AdminActivity.this, "Failed to get admin ID!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerAdmin() {
        String id = adminID.getText().toString().trim();
        String name = adminNameRes.getText().toString().trim();
        String age = adminAgeRes.getText().toString().trim();
        String gender = adminGenderRes.getText().toString().trim();
        String email = adminEmailRes.getText().toString().trim();
        String username = adminUnameRes.getText().toString().trim();
        String password = adminPassRes.getText().toString().trim();

        if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        Admin admin = new Admin(id, name, age, gender, email, username, password);

        mDatabase.child("admin").child(id).setValue(admin)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Register_AdminActivity.this, "Admin registered successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                    fetchAndIncrementAdminId(); // Get next available admin ID after success
                })
                .addOnFailureListener(e -> Toast.makeText(Register_AdminActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        adminNameRes.setText("");
        adminAgeRes.setText("");
        adminGenderRes.setText("");
        adminEmailRes.setText("");
        adminUnameRes.setText("");
        adminPassRes.setText("");
    }

    private static class Admin {
        public String id, name, age, gender, email, username, password;

        public Admin() {
        }

        public Admin(String id, String name, String age, String gender, String email, String username, String password) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.email = email;
            this.username = username;
            this.password = password;
        }
    }
}
