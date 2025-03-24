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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard_StudentActivity extends AppCompatActivity {
    String uid = "";

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

        uid = FirebaseUtils.getCurrentUserUID();
        if (uid != null) {
            Log.d("User", "Current userID logged in: " + uid);

        } else {
            Log.d("User", "No user logged in");
        }

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // store email and start the next activity
                Intent intent = new Intent(Dashboard_StudentActivity.this, Student_TestActivity.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchUserRecords(uid);
            }
        });
    }

    public void fetchUserRecords(String userUid) {
        // Get a reference to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRecordsRef = database.getReference("records").child(userUid);

        // Attach a listener to read the data
        userRecordsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Data exists for the given user UID
                    for (DataSnapshot recordSnapshot : dataSnapshot.getChildren()) {
                        // Process each record under the user UID
                        String recordId = recordSnapshot.getKey(); // Get the random hash (record ID)
                        String finalEvaluation1 = recordSnapshot.child("finalEvaluation1").getValue(String.class);
                        String finalEvaluation2 = recordSnapshot.child("finalEvaluation2").getValue(String.class);
                        long timestamp = recordSnapshot.child("timestamp").getValue(Long.class);

                        // Example: Print the fetched data
                        Log.d("User", "Record ID: " + recordId);
                        Log.d("User", "Final Evaluation 1: " + finalEvaluation1);
                        Log.d("User", "Final Evaluation 2: " + finalEvaluation2);
                        Log.d("User", "Timestamp: " + timestamp);

                        // You can also process the questions if needed
                        for (DataSnapshot questionSnapshot : recordSnapshot.getChildren()) {
                            if (questionSnapshot.getKey().startsWith("set1_question")) {
                                String questionKey = questionSnapshot.getKey();
                                String answer = questionSnapshot.getValue(String.class);
                                Log.d("test questions1", questionKey+": " +answer);
                            }
                        }
                        // You can also process the questions if needed
                        for (DataSnapshot questionSnapshot : recordSnapshot.getChildren()) {
                            if (questionSnapshot.getKey().startsWith("set2_question")) {
                                String questionKey = questionSnapshot.getKey();
                                String answer = questionSnapshot.getValue(String.class);
                                Log.d("test questions2", questionKey+": " +answer);
                            }
                        }
                    }
                } else {
                    // No data exists for the given user UID
                    System.out.println("No records found for user UID: " + userUid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                System.err.println("Failed to fetch records: " + databaseError.getMessage());
            }
        });
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard_StudentActivity.this, Student_HistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}