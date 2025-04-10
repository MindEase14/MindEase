package com.example.mindease;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin_Diagnostic_SettingActivity extends AppCompatActivity {

    private Spinner spinnerTest, spinnerName;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> nameAdapter;
    private List<String> nameItems = new ArrayList<>();
    private String defaultSetName = "";
    private String currentTestType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_diagnostic_setting);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize Spinners
        spinnerTest = findViewById(R.id.spinnerTest);
        spinnerName = findViewById(R.id.spinnerName);

        // Setup adapters with hints
        setupSpinnerAdapters();

        // Window insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button setButton = findViewById(R.id.button);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSetStatus();
            }
        });
    }

    private void updateSetStatus() {
        // Check if test type is selected
        if (spinnerTest.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a test type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if set name is selected
        if (spinnerName.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a set name", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedSetName = spinnerName.getSelectedItem().toString();
        String testType = spinnerTest.getSelectedItem().toString();
        String databasePath = testType.equals("General Anxiety Test") ?
                "generalAnxietyTestSetting" : "mindHealthTestSetting";

        // First, set all statuses to false
        databaseReference.child(databasePath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> updates = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String setName = snapshot.getKey();
                    updates.put(setName + "/status", false);
                }

                // Then set the selected one to true
                updates.put(selectedSetName + "/status", true);

                // Perform the update
                databaseReference.child(databasePath).updateChildren(updates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Admin_Diagnostic_SettingActivity.this,
                                        "Default set updated successfully!",
                                        Toast.LENGTH_SHORT).show();
                                // Refresh the spinner to show the new default
                                fetchSetNamesFromFirebase(testType);
                            } else {
                                Toast.makeText(Admin_Diagnostic_SettingActivity.this,
                                        "Failed to update default set",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Admin_Diagnostic_SettingActivity.this,
                        "Error: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinnerAdapters() {
        // For Test spinner
        List<String> testItems = new ArrayList<>();
        testItems.add("Select Test"); // Hint
        testItems.add("General Anxiety Test");
        testItems.add("Mind Health Test");

        ArrayAdapter<String> testAdapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_item,
                testItems
        );
        testAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinnerTest.setAdapter(testAdapter);
        spinnerTest.setSelection(0, false); // Show hint initially

        // For Name spinner
        nameItems.add("Select Name"); // Hint
        nameAdapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_item,
                nameItems
        );
        nameAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinnerName.setAdapter(nameAdapter);
        spinnerName.setSelection(0, false); // Show hint initially

        // Handle selections
        setupSelectionListeners();
    }

    private void setupSelectionListeners() {
        spinnerTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Hint selected, clear name spinner
                    nameItems.clear();
                    nameItems.add("Select Name");
                    nameAdapter.notifyDataSetChanged();
                    spinnerName.setSelection(0, false);
                    return;
                }

                currentTestType = parent.getItemAtPosition(position).toString();
                fetchSetNamesFromFirebase(currentTestType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void fetchSetNamesFromFirebase(String testType) {
        String databasePath;
        if (testType.equals("General Anxiety Test")) {
            databasePath = "generalAnxietyTestSetting";
        } else if (testType.equals("Mind Health Test")) {
            databasePath = "mindHealthTestSetting";
        } else {
            return;
        }

        databaseReference.child(databasePath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameItems.clear();
                nameItems.add("Select Name"); // Hint
                defaultSetName = "";

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String setName = snapshot.getKey();
                    Boolean status = snapshot.child("status").getValue(Boolean.class);

                    if (status != null && status) {
                        defaultSetName = setName;
                    }
                    nameItems.add(setName);
                }

                nameAdapter.notifyDataSetChanged();

                // Select the default set if available
                if (!defaultSetName.isEmpty()) {
                    int defaultPosition = nameItems.indexOf(defaultSetName);
                    if (defaultPosition != -1) {
                        spinnerName.setSelection(defaultPosition);
                    }
                } else {
                    spinnerName.setSelection(0, false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Admin_Diagnostic_SettingActivity.this,
                        "Failed to load set names: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}