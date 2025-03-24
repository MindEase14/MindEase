package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Student_HistoryActivity extends AppCompatActivity {
    String uid = "";
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        uid = FirebaseUtils.getCurrentUserUID();
        if (uid == null) {
            Log.d("User", "No user logged in");
            Toast.makeText(this, "Please log in to view history", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("User", "Current userID logged in: " + uid);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty list and click listener
        adapter = new HistoryAdapter(new ArrayList<>(), item -> {
            // Start detail activity when item is clicked
            Intent intent = new Intent(Student_HistoryActivity.this, RecordDetailActivity.class);
            intent.putExtra("uid", uid);
            intent.putExtra("recordKey", item.getRecordKey());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Add divider between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Fetch data from Firebase
        fetchHistoryData();
    }

    private void fetchHistoryData() {
        FirebaseHistoryHelper.fetchUserHistory(uid, new FirebaseHistoryHelper.HistoryDataListener() {
            @Override
            public void onHistoryDataLoaded(List<HistoryItem> historyItems) {
                if (historyItems.isEmpty()) {
                    Toast.makeText(Student_HistoryActivity.this, "No history records found", Toast.LENGTH_SHORT).show();
                } else {
                    // Update the adapter with new data
                    adapter.updateData(historyItems);
                }
            }

            @Override
            public void onHistoryDataError(String errorMessage) {
                Toast.makeText(Student_HistoryActivity.this, "Error loading history: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", errorMessage);
            }
        });
    }
}