package com.example.mindease;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin_Questionnaire_SettingActivity extends AppCompatActivity {

    Spinner spinnerTest, spinnerQuestionSet;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> questionSetAdapter;
    private List<String> questionSetList;
    private String currentTestType = "";
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_questionnaire_setting);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        spinnerTest = findViewById(R.id.spinnerTest);
        spinnerQuestionSet = findViewById(R.id.spinnerGroup);
        submitButton = findViewById(R.id.button); // Add this button in your XML

        // Initialize question set list and adapter
        questionSetList = new ArrayList<>();
        questionSetList.add("Select Question Set");
        questionSetAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, questionSetList);
        questionSetAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerQuestionSet.setAdapter(questionSetAdapter);
        spinnerQuestionSet.setEnabled(false);

        // Test types
        String[] tests = {"Select Test", "General Anxiety Test", "Mind Health Test"};
        ArrayAdapter<String> testAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, tests);
        testAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerTest.setAdapter(testAdapter);

        // Test selection listener
        spinnerTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedTest = adapterView.getItemAtPosition(position).toString();
                if (!selectedTest.equals("Select Test")) {
                    currentTestType = selectedTest;
                    spinnerQuestionSet.setEnabled(true);
                    questionSetList.clear();
                    questionSetList.add("Select Question Set");
                    questionSetAdapter.notifyDataSetChanged();
                    fetchQuestionSets(selectedTest);
                } else {
                    currentTestType = "";
                    spinnerQuestionSet.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        // Submit button click listener
        submitButton.setOnClickListener(v -> {
            String selectedSet = spinnerQuestionSet.getSelectedItem().toString();
            if (!selectedSet.equals("Select Question Set") && !currentTestType.isEmpty()) {
                updateQuestionSetStatus(currentTestType, selectedSet);
            } else {
                Toast.makeText(this, "Please select both test type and question set", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchQuestionSets(String testType) {
        String databasePath = getDBPath(testType);
        if (databasePath == null) return;

        databaseReference.child(databasePath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questionSetList.clear();
                questionSetList.add("Select Question Set");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String questionSetName = snapshot.getKey();
                    questionSetList.add(questionSetName);
                }

                questionSetAdapter.notifyDataSetChanged();

                if (questionSetList.size() == 1) {
                    Toast.makeText(Admin_Questionnaire_SettingActivity.this,
                            "No question sets available for this test", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Admin_Questionnaire_SettingActivity.this,
                        "Failed to load question sets: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateQuestionSetStatus(String testType, String selectedSet) {
        String databasePath = getDBPath(testType);
        if (databasePath == null) return;

        // First get all question sets to update their status
        databaseReference.child(databasePath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> updates = new HashMap<>();

                // Set all statuses to false first
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    updates.put(snapshot.getKey() + "/status", false);
                }

                // Then set the selected one to true
                updates.put(selectedSet + "/status", true);

                // Perform the update
                databaseReference.child(databasePath).updateChildren(updates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Admin_Questionnaire_SettingActivity.this,
                                        "Successfully activated " + selectedSet, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Admin_Questionnaire_SettingActivity.this,
                                        "Failed to update status: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Admin_Questionnaire_SettingActivity.this,
                        "Failed to fetch question sets: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getDBPath(String testType) {
        if (testType.equals("General Anxiety Test")) {
            return "generalAnxietyQuestionSetting";
        } else if (testType.equals("Mind Health Test")) {
            return "mindHealthQuestionSetting";
        }
        return null;
    }
}