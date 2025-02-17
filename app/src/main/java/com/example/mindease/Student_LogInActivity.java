package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Student_LogInActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private final String STATIC_EMAIL = "test";
    private final String STATIC_PASSWORD = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_log_in);

        editTextEmail = findViewById(R.id.editText_Email);
        editTextPassword = findViewById(R.id.edit_text_Password);
        Button buttonLogin = findViewById(R.id.button_login);

        // Set static default values
        editTextEmail.setText(STATIC_EMAIL);
        editTextPassword.setText(STATIC_PASSWORD);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLogin();
            }
        });
    }

    private void validateLogin() {
        String inputEmail = editTextEmail.getText().toString();
        String inputPassword = editTextPassword.getText().toString();

        if (inputEmail.equals(STATIC_EMAIL) && inputPassword.equals(STATIC_PASSWORD)) {
            // Correct login, navigate to DashboardActivity
            Intent intent = new Intent(Student_LogInActivity.this, Dashboard_StudentActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Incorrect login, show pop-up message
            Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
