package com.example.mindease;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FirebaseHistoryHelper {
    public interface HistoryDataListener {
        void onHistoryDataLoaded(List<HistoryItem> historyItems);
        void onHistoryDataError(String errorMessage);
    }

    public static void fetchUserHistory(String uid, HistoryDataListener listener) {
        DatabaseReference recordsRef = FirebaseDatabase.getInstance().getReference("records").child(uid);

        recordsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<HistoryItem> historyItems = new ArrayList<>();

                for (DataSnapshot recordSnapshot : dataSnapshot.getChildren()) {
                    Long timestamp = recordSnapshot.child("timestamp").getValue(Long.class);
                    if (timestamp != null) {
                        String date = DateUtils.formatTimestamp(timestamp);
                        historyItems.add(new HistoryItem(date, timestamp, recordSnapshot.getKey()));
                    }
                }

                historyItems.sort((o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                listener.onHistoryDataLoaded(historyItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onHistoryDataError(databaseError.getMessage());
            }
        });
    }
}