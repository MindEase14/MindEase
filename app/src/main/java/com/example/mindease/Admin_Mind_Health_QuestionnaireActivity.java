package com.example.mindease;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Admin_Mind_Health_QuestionnaireActivity extends AppCompatActivity {
    private EditText questionSetname, q1, q2, q3, q4, q5, q6, q7, q8, q9;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_mind_health_questionnaire);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize EditText fields
        questionSetname = findViewById(R.id.editTextText1);
        q1 = findViewById(R.id.editTextText2);
        q2 = findViewById(R.id.editTextText3);
        q3 = findViewById(R.id.editTextText4);
        q4 = findViewById(R.id.editTextText5);
        q5 = findViewById(R.id.editTextText6);
        q6 = findViewById(R.id.editTextText7);
        q7 = findViewById(R.id.editTextText8);
        q8 = findViewById(R.id.editTextText9);
        q9 = findViewById(R.id.editTextText10);

        Button submit = findViewById(R.id.button);

        submit.setOnClickListener(v -> saveQuestionsToFirebase());
    }

    private void saveQuestionsToFirebase() {
        String setName = questionSetname.getText().toString().trim();
        String question1 = q1.getText().toString().trim();
        String question2 = q2.getText().toString().trim();
        String question3 = q3.getText().toString().trim();
        String question4 = q4.getText().toString().trim();
        String question5 = q5.getText().toString().trim();
        String question6 = q6.getText().toString().trim();
        String question7 = q7.getText().toString().trim();
        String question8 = q8.getText().toString().trim();
        String question9 = q9.getText().toString().trim();

        // Validate inputs
        if (setName.isEmpty() || question1.isEmpty() || question2.isEmpty() ||
                question3.isEmpty() || question4.isEmpty() || question5.isEmpty() ||
                question6.isEmpty() || question7.isEmpty() || question8.isEmpty() ||
                question9.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map of questions
        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("q1", question1);
        questionMap.put("q2", question2);
        questionMap.put("q3", question3);
        questionMap.put("q4", question4);
        questionMap.put("q5", question5);
        questionMap.put("q6", question6);
        questionMap.put("q7", question7);
        questionMap.put("q8", question8);
        questionMap.put("q9", question9);
        questionMap.put("status", false);

        // Save to Firebase under mindHealthQuestionSetting/[questionSetname]
        databaseReference.child("mindHealthQuestionSetting").child(setName)
                .setValue(questionMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Admin_Mind_Health_QuestionnaireActivity.this,
                                "Questions saved successfully!", Toast.LENGTH_SHORT).show();
                        // Clear fields after successful save
                        clearFields();
                    } else {
                        Toast.makeText(Admin_Mind_Health_QuestionnaireActivity.this,
                                "Failed to save questions: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        questionSetname.setText("");
        q1.setText("");
        q2.setText("");
        q3.setText("");
        q4.setText("");
        q5.setText("");
        q6.setText("");
        q7.setText("");
        q8.setText("");
        q9.setText("");
    }
}