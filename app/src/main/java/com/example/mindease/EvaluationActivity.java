package com.example.mindease;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import java.util.HashMap;
import java.util.Map;

public class EvaluationActivity extends AppCompatActivity {

    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();

        // Simulate evaluation submission (replace with actual data collection)
        submitEvaluation("Z2nmC4em8aTQ6Jr779e1b1aSEwG3",
                "Moderate Anxiety- Consider practicing relaxation techniques...");
    }

    private void submitEvaluation(String userId, String evaluationText) {
        // Create evaluation entry
        Map<String, Object> evaluation = new HashMap<>();
        evaluation.put("finalEvaluation", evaluationText);

        // Determine anxiety level and update counts
        String anxietyLevel = determineAnxietyLevel(evaluationText);
        updateAnxietyCount(anxietyLevel);

        // Save evaluation to database
        dbRef.child("evaluations")
                .child(userId)
                .push()
                .setValue(evaluation);
    }

    private String determineAnxietyLevel(String evaluationText) {
        if (evaluationText.toLowerCase().contains("minimal")) return "minimal";
        if (evaluationText.toLowerCase().contains("mild")) return "mild";
        if (evaluationText.toLowerCase().contains("moderate")) return "moderate";
        if (evaluationText.toLowerCase().contains("severe")) return "severe";
        return "unknown";
    }

    private void updateAnxietyCount(String anxietyLevel) {
        if (!anxietyLevel.equals("unknown")) {
            dbRef.child(anxietyLevel + "Count")
                    .setValue(ServerValue.increment(1));
        }
    }
}
