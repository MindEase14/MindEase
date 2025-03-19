package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Dashboard_AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Find the view by ID
        View view2 = findViewById(R.id.view2);

        // Set an OnClickListener on the view
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the new activity
                Intent intent = new Intent(Dashboard_AdminActivity.this, Admin_Minimal_Takers_ResultActivity.class);
                startActivity(intent);
            }
        });
        // Find the view by ID
        View view = findViewById(R.id.view);

        // Set an OnClickListener on the view
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the new activity
                Intent intent = new Intent(Dashboard_AdminActivity.this, Admin_Mild_Takers_ResultActivity.class);
                startActivity(intent);
            }
        });
        // Find the view by ID
        View view3 = findViewById(R.id.view3);

        // Set an OnClickListener on the view
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the new activity
                Intent intent = new Intent(Dashboard_AdminActivity.this, Admin_Moderate_Takers_ResultActivity.class);
                startActivity(intent);
            }
        });
        // Find the view by ID
        View view4 = findViewById(R.id.view4);

        // Set an OnClickListener on the view
        view4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the new activity
                Intent intent = new Intent(Dashboard_AdminActivity.this, Admin_Moderate_Takers_ResultActivity.class);
                startActivity(intent);
            }
        });
    }
}