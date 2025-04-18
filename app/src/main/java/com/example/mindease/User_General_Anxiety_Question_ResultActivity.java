package com.example.mindease;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class User_General_Anxiety_Question_ResultActivity extends AppCompatActivity {

    private TextView textView4, textView5;
    private TextView[] questionTextViews, answerTextViews, labelTextViews, interpretationTextViews;
    private DatabaseReference recordsRef, questionSettingsRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_general_anxiety_question_result);

        uid = FirebaseUtils.getCurrentUserUID();
        if (uid == null) finish();

        initializeViews();
        setupFirebaseReferences();
        fetchLatestRecord();
    }

    private void initializeViews() {
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);

        questionTextViews = new TextView[]{
                findViewById(R.id.textView9),
                findViewById(R.id.textView16),
                findViewById(R.id.textView23),
                findViewById(R.id.textView30),
                findViewById(R.id.textView41),
                findViewById(R.id.textView49),
                findViewById(R.id.textView57)
        };

        answerTextViews = new TextView[]{
                findViewById(R.id.textView11),
                findViewById(R.id.textView18),
                findViewById(R.id.textView25),
                findViewById(R.id.textView32),
                findViewById(R.id.textView43),
                findViewById(R.id.textView51),
                findViewById(R.id.textView59)
        };

        labelTextViews = new TextView[]{
                findViewById(R.id.textView13),
                findViewById(R.id.textView20),
                findViewById(R.id.textView27),
                findViewById(R.id.textView34),
                findViewById(R.id.textView45),
                findViewById(R.id.textView53),
                findViewById(R.id.textView61)
        };

        interpretationTextViews = new TextView[]{
                findViewById(R.id.textView36),
                findViewById(R.id.textView37),
                findViewById(R.id.textView38),
                findViewById(R.id.textView39),
                findViewById(R.id.textView47),
                findViewById(R.id.textView55),
                findViewById(R.id.textView63)
        };
    }

    private void setupFirebaseReferences() {
        recordsRef = FirebaseDatabase.getInstance().getReference("records").child(uid);
        questionSettingsRef = FirebaseDatabase.getInstance().getReference("generalAnxietyQuestionSetting");
    }

    private void fetchLatestRecord() {
        recordsRef.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot latestRecord = snapshot.getChildren().iterator().next();
                    updateEvaluations(latestRecord);
                    fetchActiveQuestions(latestRecord);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateEvaluations(DataSnapshot record) {
        Long eval1 = record.child("finalEvaluation1").getValue(Long.class);
        Long eval2 = record.child("finalEvaluation2").getValue(Long.class);

        textView4.setText(getLabelForScore(eval1 != null ? eval1.intValue() : 0));
        textView5.setText(getLabelForScore(eval2 != null ? eval2.intValue() : 0));
    }

    private void fetchActiveQuestions(DataSnapshot record) {
        questionSettingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot config : snapshot.getChildren()) {
                    Boolean status = config.child("status").getValue(Boolean.class);
                    if (status != null && status) {
                        updateQuestionTexts(config, record);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateQuestionTexts(DataSnapshot config, DataSnapshot record) {
        for (int i = 0; i < 7; i++) {
            String questionKey = "q" + (i + 1);
            String answerKey = "set1_question" + (i + 1);
            String answer = record.child(answerKey).getValue(String.class);

            String question = config.child(questionKey).getValue(String.class);
            int score = answer != null ? Integer.parseInt(answer) : 0;

            questionTextViews[i].setText(question != null ? question : "Question " + (i + 1));
            answerTextViews[i].setText(String.valueOf(score));
            labelTextViews[i].setText(getLabelForScore(score));
            interpretationTextViews[i].setText(getExampleSentenceForScore(score));
        }
    }

    private String getLabelForScore(int score) {
        switch (score) {
            case 0: return "Not at all";
            case 1: return "Several days";
            case 2: return "More than half the days";
            case 3: return "Nearly every day";
            default: return "N/A";
        }
    }

    private String getExampleSentenceForScore(int score) {
        switch (score) {
            case 0: return "You haven’t experienced [symptom] over the past two weeks.";
            case 1: return "You’ve experienced [symptom] on several days in the past two weeks.";
            case 2: return "You’ve experienced [symptom] more than half the days over the past two weeks.";
            case 3: return "You’ve experienced [symptom] nearly every day over the past two weeks.";
            default: return "N/A";
        }
    }
}