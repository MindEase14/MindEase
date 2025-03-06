package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Admin_LogInActivity2 extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_log_in2);

        // Initialize views
        editTextEmail = findViewById(R.id.adminLog1);
        editTextPassword = findViewById(R.id.adminLog1);
        buttonLogin = findViewById(R.id.button_login);

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance("https://mindease-e0e70-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users/admin");

        // Login button click event
        buttonLogin.setOnClickListener(v -> {
            String emailInput = editTextEmail.getText().toString().trim();
            String passwordInput = editTextPassword.getText().toString().trim();

            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(Admin_LogInActivity2.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                checkCredentials(emailInput, passwordInput);
            }
        });
    }

    private void checkCredentials(String email, String password) {
        databaseReference.get().addOnSuccessListener(snapshot -> {
            boolean isMatch = false;

            for (DataSnapshot data : snapshot.getChildren()) {
                String storedEmail = data.child("email").getValue(String.class);
                String storedPassword = data.child("password").getValue(String.class);

                if (storedEmail != null && storedPassword != null && storedEmail.equals(email) && storedPassword.equals(password)) {
                    isMatch = true;
                    break;
                }
            }

            if (isMatch) {
                Toast.makeText(Admin_LogInActivity2.this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Admin_LogInActivity2.this, Dashboard_AdminActivity.class));
                finish();
            } else {
                Toast.makeText(Admin_LogInActivity2.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(Admin_LogInActivity2.this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}
