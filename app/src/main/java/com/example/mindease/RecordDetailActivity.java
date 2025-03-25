package com.example.mindease;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mindease.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class RecordDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String uid = getIntent().getStringExtra("uid");
        String recordKey = getIntent().getStringExtra("recordKey");

        if (uid == null || recordKey == null) {
            finish();
            return;
        }

        showRecordDialog(uid, recordKey);
    }

    private void showRecordDialog(String uid, String recordKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_record_detail, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Initialize views
        LinearLayout container = dialogView.findViewById(R.id.questionsContainer);
        TextView dateView = dialogView.findViewById(R.id.dateTextView);

        fetchRecordDetails(uid, recordKey, container, dateView, dialog);
        dialog.show();
    }

    private void fetchRecordDetails(String uid, String recordKey,
                                    LinearLayout container, TextView dateView,
                                    AlertDialog dialog) {
        DatabaseReference recordRef = FirebaseDatabase.getInstance().getReference("records")
                .child(uid)
                .child(recordKey);

        recordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle timestamp
                Long timestamp = dataSnapshot.child("timestamp").getValue(Long.class);
                if (timestamp != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                    dateView.setText(sdf.format(new Date(timestamp)));
                }

                // Organize questions by set
                Map<Integer, Map<Integer, String>> sets = new TreeMap<>();

                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    String key = questionSnapshot.getKey();
                    if ("timestamp".equals(key)) continue;

                    if (key != null && key.contains("_question")) {
                        String[] parts = key.split("_question");
                        if (parts.length == 2) {
                            try {
                                int setNumber = Integer.parseInt(parts[0].replace("set", ""));
                                int questionNumber = Integer.parseInt(parts[1]);

                                sets.putIfAbsent(setNumber, new TreeMap<>());
                                sets.get(setNumber).put(questionNumber,
                                        questionSnapshot.getValue(String.class));
                            } catch (NumberFormatException e) {
                                // Handle invalid format
                            }
                        }
                    }
                }

                // Build UI
                container.removeAllViews();
                for (Map.Entry<Integer, Map<Integer, String>> setEntry : sets.entrySet()) {
                    addSetToLayout(container, "SET " + setEntry.getKey(), setEntry.getValue());
                }

                Button closeButton = new Button(RecordDetailActivity.this);
                closeButton.setText("Close");
                closeButton.setOnClickListener(v -> {
                    dialog.dismiss();
                    finish();
                });
                container.addView(closeButton);

                dialog.setOnDismissListener(dialogInterface -> finish());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
            }
        });
    }

    private void addSetToLayout(LinearLayout container, String setName, Map<Integer, String> questions) {
        // Create set header
        TextView setHeader = new TextView(this);
        setHeader.setText(setName);
        setHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        setHeader.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(16), 0, dpToPx(8));
        setHeader.setLayoutParams(params);

        container.addView(setHeader);

        // Add questions
        for (Map.Entry<Integer, String> question : questions.entrySet()) {
            TextView questionView = new TextView(this);
            questionView.setText(String.format("Q%d\t\t%s", question.getKey(), question.getValue()));
            questionView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            LinearLayout.LayoutParams questionParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            questionParams.setMargins(dpToPx(16), 0, 0, 0);
            questionView.setLayoutParams(questionParams);

            container.addView(questionView);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}