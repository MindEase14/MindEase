package com.example.mindease;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Student_Test2Activity extends AppCompatActivity {
    private ArrayList<String> inputs = new ArrayList<>();
    private Spinner spinner5, spinner6, spinner7;
    private TextView textView17, textView21, textView22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_test2);

        initializeViews();
        setupSpinners();
        setupFirebase();
        getIntentData();
        setupSubmitButton();
    }

    private void initializeViews() {
        spinner5 = findViewById(R.id.spinner5);
        spinner6 = findViewById(R.id.spinner6);
        spinner7 = findViewById(R.id.spinner7);

        textView17 = findViewById(R.id.textView17);
        textView21 = findViewById(R.id.textView21);
        textView22 = findViewById(R.id.textView22);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                this,
                R.layout.custom_spinner_item,
                getResources().getStringArray(R.array.spinner_options)
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(Color.BLACK);
                return view;
            }
        };

        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

        spinner5.setAdapter(adapter);
        spinner6.setAdapter(adapter);
        spinner7.setAdapter(adapter);
    }

    private void setupFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                .child("generalAnxietyQuestionSetting");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataSnapshot activeConfig = findActiveConfig(snapshot);

                if (activeConfig == null) {
                    showErrorAndFinish("No active questionnaire found");
                    return;
                }

                try {
                    textView17.setText("5. " + activeConfig.child("q5").getValue(String.class));
                    textView21.setText("6. " + activeConfig.child("q6").getValue(String.class));
                    textView22.setText("7. " + activeConfig.child("q7").getValue(String.class));
                } catch (Exception e) {
                    showErrorAndFinish("Error loading questions");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showErrorAndFinish("Database error: " + error.getMessage());
            }
        });
    }

    private DataSnapshot findActiveConfig(DataSnapshot snapshot) {
        for (DataSnapshot config : snapshot.getChildren()) {
            if (config.hasChild("status") &&
                    Boolean.TRUE.equals(config.child("status").getValue(Boolean.class))) {
                return config;
            }
        }
        return null;
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("inputs")) {
            ArrayList<String> passedInputs = intent.getStringArrayListExtra("inputs");
            if (passedInputs != null) {
                inputs = passedInputs;
                // Log inputs for debugging
                for (int i = 0; i < passedInputs.size(); i++) {
                    Log.d("Student_Test2", "Input " + i + ": " + passedInputs.get(i));
                }
            } else {
                showToast("No data received!");
            }
        }
    }

    private void setupSubmitButton() {
        Button button5 = findViewById(R.id.button5);
        button5.setOnClickListener(v -> {
            if (validateSelections()) {
                ArrayList<String> allInputs = appendInputs();
                Intent intent = new Intent(this, Student_Test3Activity.class);
                intent.putStringArrayListExtra("inputs", allInputs);
                startActivity(intent);
            }
        });
    }

    private ArrayList<String> appendInputs() {
        inputs.add(String.valueOf(spinner5.getSelectedItemPosition() - 1)); // Subtract 1
        inputs.add(String.valueOf(spinner6.getSelectedItemPosition() - 1)); // Subtract 1
        inputs.add(String.valueOf(spinner7.getSelectedItemPosition() - 1)); // Subtract 1
        return inputs;
    }

    private boolean validateSelections() {
        boolean isValid = true;

        if (spinner5.getSelectedItemPosition() == 0) {
            showToast("Please select an option for question 5");
            isValid = false;
        }
        if (spinner6.getSelectedItemPosition() == 0) {
            showToast("Please select an option for question 6");
            isValid = false;
        }
        if (spinner7.getSelectedItemPosition() == 0) {
            showToast("Please select an option for question 7");
            isValid = false;
        }
        return isValid;
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}