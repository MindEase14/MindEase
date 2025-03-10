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

public class Student_RegisterActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText studentID, stuNameRes, stuAgeRes, stuGenderRes, stuEmailRes, stuUnameRes, stuPassRes;
    private Button studentButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://mindease-e0e70-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        // Initialize UI elements
        studentID = findViewById(R.id.studentID);
        stuNameRes = findViewById(R.id.stuNameRes);
        stuAgeRes = findViewById(R.id.stuAgeRes);
        stuGenderRes = findViewById(R.id.stuGenderRes);
        stuEmailRes = findViewById(R.id.stuEmailRes);
        stuUnameRes = findViewById(R.id.stuUnameRes);
        stuPassRes = findViewById(R.id.stuPassRes);
        studentButt = findViewById(R.id.studentButt);

        studentID.setEnabled(false); // Make student ID read-only
        fetchStudentId(); // Fetch ID when activity starts

        studentButt.setOnClickListener(v -> registerStudent());
    }

    private void fetchStudentId() {
        mDatabase.child("studentIdCount").runTransaction(new Transaction.Handler() {
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
                    runOnUiThread(() -> studentID.setText(String.valueOf(newId)));
                } else {
                    Log.e("Firebase", "ID fetch failed: " + error);
                    runOnUiThread(() -> Toast.makeText(Student_RegisterActivity.this,
                            "Failed to get student ID!", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void registerStudent() {
        String id = studentID.getText().toString().trim();
        String name = stuNameRes.getText().toString().trim();
        String age = stuAgeRes.getText().toString().trim();
        String gender = stuGenderRes.getText().toString().trim();
        String email = stuEmailRes.getText().toString().trim();
        String username = stuUnameRes.getText().toString().trim();
        String password = stuPassRes.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || age.isEmpty() || gender.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserData(user.getUid(), id, name, age, gender, email, username, password);
                        }
                    } else {
                        Toast.makeText(Student_RegisterActivity.this,
                                "Registration Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String uid, String studentId, String name, String age,
                              String gender, String email, String username, String password) {
        Student student = new Student(studentId, name, age, gender, email, username, password);

        mDatabase.child("users/students").child(uid).setValue(student)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Student_RegisterActivity.this,
                            "Registration successful!", Toast.LENGTH_SHORT).show();
                    loginUser(email, password);
                })
                .addOnFailureListener(e -> Toast.makeText(Student_RegisterActivity.this,
                        "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, Student_LogInActivity.class));
                        finish();
                    } else {
                        Toast.makeText(Student_RegisterActivity.this,
                                "Auto-login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static class Student {
        public String id, name, age, gender, email, username, password;

        public Student() {}

        public Student(String id, String name, String age, String gender,
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