package com.example.mindease;

import android.os.Bundle;
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

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create static data
        List<HistoryItem> historyItems = new ArrayList<>();
        historyItems.add(new HistoryItem("2023-11-15", "Completed stress management workshop"));
        historyItems.add(new HistoryItem("2023-11-10", "Counseling session with Dr. Johnson"));
        historyItems.add(new HistoryItem("2023-11-05", "Submitted weekly mood tracker"));
        historyItems.add(new HistoryItem("2023-10-28", "Attended group therapy session"));
        historyItems.add(new HistoryItem("2023-10-20", "Completed depression screening"));
        historyItems.add(new HistoryItem("2023-10-15", "Initial consultation with counselor"));

        // Set up adapter
        adapter = new HistoryAdapter(historyItems);
        recyclerView.setAdapter(adapter);

        // Add divider between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}
