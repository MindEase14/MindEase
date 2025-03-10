package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Dashboard_StudentActivity extends AppCompatActivity {
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the data passed from the previous activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email")) {
            email = intent.getStringExtra("email");

            // Check if textInputs is null before using it
            if (email != null) {
                // Log the email
                Log.d("User", "Email: " + email);
            } else {
                Toast.makeText(this, "User email not found!", Toast.LENGTH_SHORT).show();
            }
        } else{
            // Handle the case where no data is passed
            Toast.makeText(this, "No data received!", Toast.LENGTH_SHORT).show();
        }


        Button button1 = findViewById(R.id.button1); // Find button by its ID

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // store email and start the next activity
                Intent intent = new Intent(Dashboard_StudentActivity.this, Student_TestActivity.class);
                intent.putExtra("email", email); // Pass the email to the next activity
                startActivity(intent);
            }
        });
    }
}