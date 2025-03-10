package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

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
                .getReference();

        // Initialize UI elements
        adminID = findViewById(R.id.adminID);
        adminNameRes = findViewById(R.id.adminNameRes);
        adminAgeRes = findViewById(R.id.adminAgeRes);
        adminGenderRes = findViewById(R.id.adminGenderRes);
        adminEmailRes = findViewById(R.id.adminEmailRes);
        adminUnameRes = findViewById(R.id.adminUnameRes);
        adminPassRes = findViewById(R.id.adminPassRes);
        adminButt = findViewById(R.id.adminButt);

        adminID.setEnabled(false); // Make admin ID read-only
        fetchAdminId(); // Fetch ID when activity starts

        adminButt.setOnClickListener(v -> registerAdmin());
    }

    private void fetchAdminId() {
        mDatabase.child("adminIdCount").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentId = mutableData.getValue(Integer.class);
                if (currentId == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentId + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (committed && snapshot.exists()) {
                    int newId = snapshot.getValue(Integer.class);
                    runOnUiThread(() -> adminID.setText(String.valueOf(newId)));
                } else {
                    Log.e("Firebase", "Admin ID fetch failed: " + error);
                    runOnUiThread(() -> Toast.makeText(Register_AdminActivity.this,
                            "Failed to get admin ID!", Toast.LENGTH_SHORT).show());
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

        if (id.isEmpty() || name.isEmpty() || age.isEmpty() || gender.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveAdminData(user.getUid(), id, name, age, gender, email, username, password);
                        }
                    } else {
                        Toast.makeText(Register_AdminActivity.this,
                                "Registration Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveAdminData(String uid, String adminId, String name, String age,
                               String gender, String email, String username, String password) {
        Admin admin = new Admin(adminId, name, age, gender, email, username, password);

        mDatabase.child("users/admin").child(uid).setValue(admin)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Register_AdminActivity.this,
                            "Registration successful!", Toast.LENGTH_SHORT).show();
                    loginUser(email, password);
                })
                .addOnFailureListener(e -> Toast.makeText(Register_AdminActivity.this,
                        "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, Admin_LogInActivity.class));
                        finish();
                    } else {
                        Toast.makeText(Register_AdminActivity.this,
                                "Auto-login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static class Admin {
        public String id, name, age, gender, email, username, password;

        public Admin() {}

        public Admin(String id, String name, String age, String gender,
                     String email, String username, String password) {
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