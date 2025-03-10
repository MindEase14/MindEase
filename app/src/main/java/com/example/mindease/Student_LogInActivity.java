package com.example.mindease;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Student_LogInActivity extends AppCompatActivity {

    private EditText studentEmail, studentPass;
    private Button studentButt;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_log_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        studentEmail = findViewById(R.id.studentEmail);
        studentPass = findViewById(R.id.studentPass);
        studentButt = findViewById(R.id.studentButt);

        // Setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Authenticating");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        studentButt.setOnClickListener(v -> {
            String emailInput = studentEmail.getText().toString().trim();
            String passwordInput = studentPass.getText().toString().trim();

            if (validateInputs(emailInput, passwordInput)) {
                performLogin(emailInput, passwordInput);

                // store email and start the next activity
                String email = studentEmail.getText().toString();
                Intent intent = new Intent(Student_LogInActivity.this, Dashboard_StudentActivity.class);
                intent.putExtra("email", email); // Pass the email to the next activity
                startActivity(intent);
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        boolean valid = true;

        if (email.isEmpty()) {
            studentEmail.setError("Email is required");
            studentEmail.requestFocus();
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            studentEmail.setError("Valid email required");
            studentEmail.requestFocus();
            valid = false;
        }

        if (password.isEmpty()) {
            studentPass.setError("Password is required");
            studentPass.requestFocus();
            valid = false;
        }

        return valid;
    }

    private void performLogin(String email, String password) {
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        handleSuccessfulLogin(user);
                    } else {
                        handleLoginError(task.getException());
                    }
                });
    }

    private void handleSuccessfulLogin(FirebaseUser user) {
        if (user != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Login Successful")
                    .setMessage("Welcome " + user.getEmail())
                    .setPositiveButton("Continue", (dialog, which) -> {
                        redirectToDashboard();
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void redirectToDashboard() {
        startActivity(new Intent(this, Dashboard_StudentActivity.class));
        finish();
    }

    private void handleLoginError(Exception exception) {
        String errorMessage = "Authentication failed";

        if (exception != null && exception.getMessage() != null) {
            String error = exception.getMessage().toLowerCase();
            if (error.contains("invalid password")) {
                errorMessage = "Incorrect password";
            } else if (error.contains("user not found")) {
                errorMessage = "Account not found";
            } else if (error.contains("network error")) {
                errorMessage = "No internet connection";
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}