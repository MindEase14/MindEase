package com.example.mindease;

import android.content.Intent;
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

public class Admin_LogInActivity2 extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_log_in2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        editTextEmail = findViewById(R.id.editText_Email);
        editTextPassword = findViewById(R.id.edit_text_Password);
        buttonLogin = findViewById(R.id.button_login);

        // Set default values
        editTextEmail.setText("test");
        editTextPassword.setText("test");

        // Login button click event
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.equals("test") && password.equals("test")) {
                    // Open Dashboard_AdminActivity
                    Intent intent = new Intent(Admin_LogInActivity2.this, Dashboard_AdminActivity.class);
                    startActivity(intent);
                    finish(); // Close login activity
                } else {
                    // Show incorrect credentials message
                    Toast.makeText(Admin_LogInActivity2.this, "Email or password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
