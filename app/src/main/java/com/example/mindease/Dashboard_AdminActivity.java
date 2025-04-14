package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

import java.util.Calendar;
import java.util.Map;

public class Dashboard_AdminActivity extends AppCompatActivity {

    private TextView minimalTakers, mildTakers, moderateTakers, severeTakers, textView6;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);

        initializeViews();
        setupFirebase();
        fetchRecordsAndUpdateCounts();
        fetchAdminUsername();
        View view2 = findViewById(R.id.view2);
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent intent = new Intent(Dashboard_AdminActivity.this, Admin_Severe_Takers_ResultActivity.class);
                startActivity(intent);
            }
        });
        View view5 = findViewById(R.id.view5);
        view5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard_AdminActivity.this, Admin_Set_DiagnosticActivity.class);
                startActivity(intent);
            }
        });
        View view6 = findViewById(R.id.view6);
        view6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard_AdminActivity.this, Admin_General_Set_QuestionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeViews() {
        minimalTakers = findViewById(R.id.minimalTakers);
        mildTakers = findViewById(R.id.mildTakers);
        moderateTakers = findViewById(R.id.moderateTakers);
        severeTakers = findViewById(R.id.severeTakers);
        textView6 = findViewById(R.id.textView6);
    }

    private void setupFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
    }

    private void fetchAdminUsername() {
        String adminId = "avZbgjvFJTYiy01keRp7uutd5473";
        dbRef.child("users/admin").child(adminId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("username").getValue(String.class);
                    textView6.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch admin name: " + error.getMessage());
            }
        });
    }

    private void fetchRecordsAndUpdateCounts() {
        dbRef.child("records").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int minimal = 0, mild = 0, moderate = 0, severe = 0;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot evalSnapshot : userSnapshot.getChildren()) {
                        try {
                            Map<String, Object> eval = (Map<String, Object>) evalSnapshot.getValue();
                            if (eval == null) continue;

                            // Safely get evaluation and timestamp
                            Object evalObj = eval.get("finalEvaluation1");
                            Object timestampObj = eval.get("timestamp");

                            if (evalObj == null || timestampObj == null) continue;

                            String evaluationText = evalObj.toString().toLowerCase();
                            String timestamp = timestampObj.toString();

                            // Skip if not current month
                            if (!isCurrentMonth(timestamp)) continue;

                            // Count based on evaluation text
                            if (evaluationText.contains("minimal") || evaluationText.contains("1")) minimal++;
                            else if (evaluationText.contains("mild") || evaluationText.contains("2")) mild++;
                            else if (evaluationText.contains("moderate") || evaluationText.contains("3")) moderate++;
                            else if (evaluationText.contains("severe") || evaluationText.contains("4")) severe++;
                        } catch (Exception e) {
                            Log.e("DataError", "Error processing record: " + e.getMessage());
                        }
                    }
                }

                // Update UI on main thread
                int finalMinimal = minimal;
                int finalMild = mild;
                int finalModerate = moderate;
                int finalSevere = severe;
                runOnUiThread(() -> {
                    minimalTakers.setText(String.valueOf(finalMinimal));
                    mildTakers.setText(String.valueOf(finalMild));
                    moderateTakers.setText(String.valueOf(finalModerate));
                    severeTakers.setText(String.valueOf(finalSevere));
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch records: " + error.getMessage());
            }
        });
    }

    private boolean isCurrentMonth(String timestampStr) {
        if (timestampStr == null || timestampStr.isEmpty()) {
            return false;
        }

        try {
            long timestamp = Long.parseLong(timestampStr);
            Calendar evalCal = Calendar.getInstance();
            evalCal.setTimeInMillis(timestamp);

            Calendar currentCal = Calendar.getInstance();
            return evalCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH)
                    && evalCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR);
        } catch (NumberFormatException e) {
            Log.e("TimestampError", "Invalid timestamp format: " + timestampStr);
            return false;
        }
    }
}