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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Admin_LogInActivity2 extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_log_in2);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        editTextEmail = findViewById(R.id.adminLog1);
        editTextPassword = findViewById(R.id.adminLog2);
        buttonLogin = findViewById(R.id.button_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Authenticating");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                performLogin(email, password);
            } else {
                showError("Email and password are required");
            }
        });
    }

    private void performLogin(String email, String password) {
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkAdminAccess(user);
                        }
                    } else {
                        showError(task.getException() != null ? task.getException().getMessage() : "Authentication failed");
                    }
                });
    }


    private void checkAdminAccess(FirebaseUser user) {
        if (user == null) return;

        databaseReference.child("admin").child(user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        redirectToDashboard();
                    } else {
                        showNotAdminAlert();
                    }
                });
    }

    private void showNotAdminAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Access Denied")
                .setMessage("Not an Admin Account")
                .setPositiveButton("OK", (dialog, which) -> redirectToMainActivity())
                .setCancelable(false)
                .show();
    }

    private void redirectToDashboard() {
        startActivity(new Intent(this, Dashboard_AdminActivity.class));
        finish();
    }

    private void redirectToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
