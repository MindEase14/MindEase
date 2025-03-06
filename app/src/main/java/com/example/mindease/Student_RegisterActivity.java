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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Student_RegisterActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText studentID, stuNameRes, stuAgeRes, stuGenderRes, stuEmailRes, stuUnameRes, stuPassRes;
    private Button studentButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_register);

        mDatabase = FirebaseDatabase.getInstance("https://mindease-e0e70-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users");

        // Initialize UI elements
        studentID = findViewById(R.id.studentID);
        stuNameRes = findViewById(R.id.stuNameRes);
        stuAgeRes = findViewById(R.id.stuAgeRes);
        stuGenderRes = findViewById(R.id.stuGenderRes);
        stuEmailRes = findViewById(R.id.stuEmailRes);
        stuUnameRes = findViewById(R.id.stuUnameRes);
        stuPassRes = findViewById(R.id.stuPassRes);
        studentButt = findViewById(R.id.studentButt);

        // Make studentID read-only
        studentID.setEnabled(false);

        // Fetch and update the student ID properly
        fetchAndIncrementStudentId();

        studentButt.setOnClickListener(v -> registerStudent());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchAndIncrementStudentId() {
        mDatabase.child("studentIdCount").runTransaction(new com.google.firebase.database.Transaction.Handler() {
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
                    int newStudentId = snapshot.getValue(Integer.class);
                    studentID.setText(String.valueOf(newStudentId)); // Set the new ID to text field
                } else {
                    Toast.makeText(Student_RegisterActivity.this, "Failed to get student ID!", Toast.LENGTH_SHORT).show();
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

        if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        Student student = new Student(id, name, age, gender, email, username, password);

        mDatabase.child("students").child(id).setValue(student)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Student_RegisterActivity.this, "Student registered successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                    fetchAndIncrementStudentId(); // Get next available student ID after success
                })
                .addOnFailureListener(e -> Toast.makeText(Student_RegisterActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        stuNameRes.setText("");
        stuAgeRes.setText("");
        stuGenderRes.setText("");
        stuEmailRes.setText("");
        stuUnameRes.setText("");
        stuPassRes.setText("");
    }

    private static class Student {
        public String id, name, age, gender, email, username, password;

        public Student() {
        }

        public Student(String id, String name, String age, String gender, String email, String username, String password) {
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
