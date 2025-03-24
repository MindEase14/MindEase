package com.example.mindease;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecordDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        String uid = getIntent().getStringExtra("uid");
        String recordKey = getIntent().getStringExtra("recordKey");

        if (uid == null || recordKey == null) {
            finish();
            return;
        }

        fetchRecordDetails(uid, recordKey);
    }

    private void fetchRecordDetails(String uid, String recordKey) {
        DatabaseReference recordRef = FirebaseDatabase.getInstance().getReference("records")
                .child(uid)
                .child(recordKey);

        recordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder details = new StringBuilder();

                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    String key = questionSnapshot.getKey();

                    // Skip timestamp and any other non-question fields
                    if ("timestamp".equals(key)) {
                        continue;
                    }

                    // Parse set number and question number from keys like "set1_question2"
                    if (key != null && key.contains("_question")) {
                        String[] parts = key.split("_question");
                        if (parts.length == 2) {
                            String setNumber = parts[0].replace("set", "");
                            String questionNumber = parts[1];
                            details.append("set").append(setNumber).append(" - Q").append(questionNumber)
                                    .append(": ")
                                    .append(questionSnapshot.getValue(String.class))
                                    .append("\n\n");
                        }
                    }
                }

                TextView detailsTextView = findViewById(R.id.dateTextView);
                detailsTextView.setText(details.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}