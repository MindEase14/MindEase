package com.example.mindease;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Locale;

public class Admin_Severe_Takers_ResultActivity extends AppCompatActivity
        implements SevereUserAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private TextView moderateCount2, textView6;
    private DatabaseReference dbRef;
    private SevereUserAdapter adapter;
    private final List<User> userList = new ArrayList<>();
    private int currentModerateCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_severe_takers_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        moderateCount2 = findViewById(R.id.textView8);
        textView6 = findViewById(R.id.textView6);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SevereUserAdapter(userList, this);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();

        loadData();
        fetchAdminUsername();
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

    private void loadData() {
        dbRef.child("records").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                currentModerateCount = 0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    processUserEvaluations(userId, userSnapshot);
                }
                moderateCount2.setText(String.valueOf(currentModerateCount));
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
                if (evaluationText.contains("severe")) { // Changed from "moderate" to "severe"
                    Object timestampObj = evaluation.get("timestamp");
                    String timestamp = timestampObj != null ? timestampObj.toString() : "";
                    if (isCurrentMonth(timestamp)) {
                        currentModerateCount++;
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
                if (snapshot.exists()) {
                    StringBuilder message = new StringBuilder();
                    String name = user.getName();

                    message.append("Name: ").append(name).append("\n\nSET 1:\n");
                    for (int i = 1; i <= 7; i++) {
                        String question = getSet1Question(i);
                        String response = snapshot.child("set1_question" + i).getValue(String.class);
                        message.append("Question ").append(i).append(": ").append(question).append("\n")
                                .append("Response: ").append(response != null ? response : "N/A").append("\n\n");
                    }

                    message.append("SET 2:\n");
                    for (int i = 1; i <= 9; i++) {
                        String question = getSet2Question(i);
                        String response = snapshot.child("set2_question" + i).getValue(String.class);
                        message.append("Question ").append(i).append(": ").append(question).append("\n")
                                .append("Response: ").append(response != null ? response : "N/A").append("\n\n");
                    }

                    String evaluation1 = snapshot.child("finalEvaluation1").getValue(String.class);
                    String evaluation2 = snapshot.child("finalEvaluation2").getValue(String.class);
                    message.append("Evaluation 1: ").append(evaluation1 != null ? evaluation1 : "N/A").append("\n");
                    message.append("Evaluation 2: ").append(evaluation2 != null ? evaluation2 : "N/A").append("\n");

                    new AlertDialog.Builder(Admin_Severe_Takers_ResultActivity.this)
                            .setTitle(user.getName() + "'s Test Results")
                            .setMessage(message.toString())
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

    private String getSet1Question(int questionNumber) {
        switch (questionNumber) {
            case 1: return "Feeling nervous, anxious, or on edge";
            case 2: return "Not being able to stop or control worrying";
            case 3: return "Worrying too much about different things";
            case 4: return "Trouble relaxing";
            case 5: return "Being so restless that it's hard to sit still";
            case 6: return "Becoming easily annoyed or irritable";
            case 7: return "Feeling afraid as if something awful might happen";
            default: return "Unknown question";
        }
    }

    private String getSet2Question(int questionNumber) {
        switch (questionNumber) {
            case 1: return "Little interest or pleasure in doing things";
            case 2: return "Feeling down, depressed, or hopeless";
            case 3: return "Trouble falling or staying asleep, or sleeping too much";
            case 4: return "Feeling tired or having little energy";
            case 5: return "Poor appetite or overeating";
            case 6: return "Feeling bad about yourself — or that you are a failure or have let yourself or your family down";
            case 7: return "Trouble concentrating on things, such as reading the newspaper or watching television";
            case 8: return "Moving or speaking so slowly that other people could have noticed, or the opposite – being so fidgety or restless that you have been moving around more than usual";
            case 9: return "Thoughts that you would be better off dead or hurting yourself in some way";
            default: return "Unknown question";
        }
    }
}