package com.example.mindease;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Locale;

public class Admin_Minimal_Takers_ResultActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private TextView minimalCount2, textView6;
    private DatabaseReference dbRef;
    private UserAdapter adapter;
    private final List<User> userList = new ArrayList<>();
    private int currentMinimalCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_minimal_takers_result);

        minimalCount2 = findViewById(R.id.minimalCount2);
        textView6 = findViewById(R.id.textView6);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();

        loadData();
        fetchAdminUsername(); // Fetch admin's username
    }

    private void fetchAdminUsername() {
        // Assuming adminId is retrieved from SharedPreferences or another source
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

    private void loadData() {
        dbRef.child("records").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                currentMinimalCount = 0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    processUserEvaluations(userId, userSnapshot);
                }
                minimalCount2.setText(String.valueOf(currentMinimalCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to load records: " + error.getMessage());
            }
        });
    }

    private void processUserEvaluations(String userId, DataSnapshot userSnapshot) {
        for (DataSnapshot evalSnapshot : userSnapshot.getChildren()) {
            Map<String, Object> evaluation = (Map<String, Object>) evalSnapshot.getValue();
            if (evaluation != null && evaluation.containsKey("finalEvaluation1")) {
                String evaluationText = evaluation.get("finalEvaluation1").toString().toLowerCase();
                if (evaluationText.contains("minimal")) {
                    Object timestampObj = evaluation.get("timestamp");
                    String timestamp = timestampObj != null ? timestampObj.toString() : "";
                    if (isCurrentMonth(timestamp)) {
                        currentMinimalCount++;
                        String evaluationKey = evalSnapshot.getKey();
                        fetchUserDetails(userId, timestamp, evaluationKey);
                    }
                }
            }
        }
    }

    private boolean isCurrentMonth(String timestampStr) {
        try {
            long timestamp = Long.parseLong(timestampStr);
            Calendar evalCal = Calendar.getInstance();
            evalCal.setTimeInMillis(timestamp);

            Calendar currentCal = Calendar.getInstance();
            return evalCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH)
                    && evalCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void fetchUserDetails(String userId, String timestamp, String evaluationKey) {
        dbRef.child("users/admin").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot adminSnapshot) {
                if (adminSnapshot.exists()) {
                    String userName = adminSnapshot.child("name").getValue(String.class);
                    addUserToList(userName, timestamp, userId, evaluationKey);
                } else {
                    dbRef.child("users/students").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
                            String userName = studentSnapshot.child("name").getValue(String.class);
                            addUserToList(userName, timestamp, userId, evaluationKey);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FirebaseError", "Failed to fetch student: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch admin: " + error.getMessage());
            }
        });
    }

    private void addUserToList(String userName, String timestamp, String userId, String evaluationKey) {
        if (userName != null && timestamp != null) {
            String formattedDate = formatTimestamp(timestamp);
            userList.add(new User(userName, formattedDate, userId, evaluationKey));
            adapter.notifyDataSetChanged();
        }
    }

    private String formatTimestamp(String timestamp) {
        try {
            long epochTime = Long.parseLong(timestamp);
            return new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault())
                    .format(new Date(epochTime));
        } catch (NumberFormatException e) {
            return "Invalid date";
        }
    }

    @Override
    public void onItemClick(User user) {
        showUserDetailsDialog(user);
    }

    private void showUserDetailsDialog(User user) {
        String userId = user.getUserId();
        String evaluationKey = user.getEvaluationKey();

        dbRef.child("records").child(userId).child(evaluationKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> eval = (Map<String, Object>) snapshot.getValue();
                if (eval != null) {
                    String timestamp = eval.get("timestamp").toString();
                    String evaluation = eval.get("finalEvaluation1").toString();
                    String formattedDate = formatTimestamp(timestamp);

                    new AlertDialog.Builder(Admin_Minimal_Takers_ResultActivity.this)
                            .setTitle(user.getName() + "'s Test Details")
                            .setMessage("Date: " + formattedDate + "\nEvaluation: " + evaluation)
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch evaluation details: " + error.getMessage());
            }
        });
    }
}
