package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
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
        if (uid == null) {
            Log.d("User", "No user logged in");
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView usernameTextView = findViewById(R.id.textView6);
        DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child("students")
                .child(uid)
                .child("username");

        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.getValue(String.class);
                    usernameTextView.setText(username);
                } else {
                    usernameTextView.setText("Username not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Username fetch failed: " + databaseError.getMessage());
                usernameTextView.setText("Error loading username");
            }
        });

        Log.d("User", "Current userID logged in: " + uid);

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);

        button1.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard_StudentActivity.this, Student_TestActivity.class);
            startActivity(intent);
        });

        button2.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard_StudentActivity.this, Student_HistoryActivity.class);
            startActivity(intent);
        });
    }

    public void fetchUserRecords(String userUid) {
        if (userUid == null || userUid.isEmpty()) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRecordsRef = database.getReference("records").child(userUid);

        userRecordsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot recordSnapshot : dataSnapshot.getChildren()) {
                        String recordId = recordSnapshot.getKey();
                        String finalEvaluation1 = recordSnapshot.child("finalEvaluation1").getValue(String.class);
                        String finalEvaluation2 = recordSnapshot.child("finalEvaluation2").getValue(String.class);
                        Long timestamp = recordSnapshot.child("timestamp").getValue(Long.class);

                        Log.d("User", "Record ID: " + recordId);
                        Log.d("User", "Final Evaluation 1: " + finalEvaluation1);
                        Log.d("User", "Final Evaluation 2: " + finalEvaluation2);
                        Log.d("User", "Timestamp: " + timestamp);

                        for (DataSnapshot questionSnapshot : recordSnapshot.getChildren()) {
                            String questionKey = questionSnapshot.getKey();
                            if (questionKey != null) {
                                if (questionKey.startsWith("set1_question")) {
                                    String answer = questionSnapshot.getValue(String.class);
                                    Log.d("test questions1", questionKey + ": " + answer);
                                } else if (questionKey.startsWith("set2_question")) {
                                    String answer = questionSnapshot.getValue(String.class);
                                    Log.d("test questions2", questionKey + ": " + answer);
                                }
                            }
                        }
                    }
                } else {
                    Log.d("User", "No records found for user UID: " + userUid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to fetch records: " + databaseError.getMessage());
                Toast.makeText(Dashboard_StudentActivity.this,
                        "Failed to load records", Toast.LENGTH_SHORT).show();
            }
        });
    }
}