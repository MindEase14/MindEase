package com.example.mindease;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Admin_LogInActivity2 extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_log_in2);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        editTextEmail = findViewById(R.id.adminLog1);
        editTextPassword = findViewById(R.id.adminLog2);
        buttonLogin = findViewById(R.id.button_login);

        // Setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Authenticating");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        buttonLogin.setOnClickListener(v -> {
            String emailInput = editTextEmail.getText().toString().trim();
            String passwordInput = editTextPassword.getText().toString().trim();

            if (validateInputs(emailInput, passwordInput)) {
                performLogin(emailInput, passwordInput);
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        boolean valid = true;

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Valid email required");
            editTextEmail.requestFocus();
            valid = false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
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
        startActivity(new Intent(this, Dashboard_AdminActivity.class));
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