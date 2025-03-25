package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Student_HistoryActivity extends AppCompatActivity {
    private String uid;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private TextView textView6, textView10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_history);

        // Initialize UI components
        textView6 = findViewById(R.id.textView6);
        textView10 = findViewById(R.id.textView10);
        recyclerView = findViewById(R.id.recyclerView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get current user ID
        uid = FirebaseUtils.getCurrentUserUID();
        if (uid == null) {
            handleUnauthorizedAccess();
            return;
        }

        setupRecyclerView();
        fetchUserData();
        fetchHistoryData();
    }

    private void handleUnauthorizedAccess() {
        Toast.makeText(this, "Please log in to view history", Toast.LENGTH_SHORT).show();
        Log.d("Auth", "No valid user session");
        finish();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(new ArrayList<>(), item -> {
            Intent intent = new Intent(this, RecordDetailActivity.class);
            intent.putExtra("uid", uid);
            intent.putExtra("recordKey", item.getRecordKey());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void fetchUserData() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child("students")
                .child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    runOnUiThread(() -> {
                        textView6.setText(username != null ? username : "Username not available");
                        textView10.setText(email != null ? email : "Email not available");
                    });
                } else {
                    Log.d("Firebase", "User data not found at path: " + userRef.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error loading user data: " + databaseError.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(Student_HistoryActivity.this,
                                "Error loading profile data", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void fetchHistoryData() {
        FirebaseHistoryHelper.fetchUserHistory(uid, new FirebaseHistoryHelper.HistoryDataListener() {
            @Override
            public void onHistoryDataLoaded(List<HistoryItem> historyItems) {
                if (historyItems.isEmpty()) {
                    Toast.makeText(Student_HistoryActivity.this,
                            "No history records found", Toast.LENGTH_SHORT).show();
                }
                adapter.updateData(historyItems);
            }

            @Override
            public void onHistoryDataError(String errorMessage) {
                Log.e("HistoryError", errorMessage);
                Toast.makeText(Student_HistoryActivity.this,
                        "Error loading history: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}