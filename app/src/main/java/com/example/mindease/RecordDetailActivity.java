package com.example.mindease;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

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

                // Skip timestamp as we already show the date in the title
                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    if (!"timestamp".equals(questionSnapshot.getKey())) {
                        details.append(questionSnapshot.getKey())
                                .append(": ")
                                .append(questionSnapshot.getValue(String.class))
                                .append("\n\n");
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