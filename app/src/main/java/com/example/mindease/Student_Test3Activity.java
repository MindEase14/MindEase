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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Student_Test3Activity extends AppCompatActivity {
    private ArrayList<String> textInputs = new ArrayList<>();
    private Spinner spinner1, spinner2, spinner3;
    private TextView textView2, textView3, textView4, textView5, textView6, textView7, textView8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_test3);

        initializeViews();
        setupSpinners();
        setupFirebase();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve inputs from previous activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("inputs")) {
            ArrayList<String> passedInputs = intent.getStringArrayListExtra("inputs");
            if (passedInputs != null) {
                textInputs = passedInputs;
                for (int i = 0; i < passedInputs.size(); i++) {
                    Log.d("Student_Test3", "Input " + i + ": " + passedInputs.get(i));
                }
            } else {
                showToast("No data received!");
            }
        }

        setupSubmitButton();
    }

    private void initializeViews() {
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);

        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);
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

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);
    }

    private void setupFirebase() {
        DatabaseReference testSettingsRef = FirebaseDatabase.getInstance().getReference()
                .child("mindHealthTestSetting");

        testSettingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataSnapshot activeLabelConfig = findActiveConfig(snapshot);
                if (activeLabelConfig == null) {
                    showErrorAndFinish("No active label configuration");
                    return;
                }

                try {
                    String label0 = activeLabelConfig.child("0").getValue(String.class);
                    String label1 = activeLabelConfig.child("1").getValue(String.class);
                    String label2 = activeLabelConfig.child("2").getValue(String.class);
                    String label3 = activeLabelConfig.child("3").getValue(String.class);

                    textView2.setText(label0);
                    textView6.setText(label1);
                    textView7.setText(label2);
                    textView8.setText(label3);
                } catch (Exception e) {
                    showErrorAndFinish("Error loading labels");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showErrorAndFinish("Label config error: " + error.getMessage());
            }
        });

        DatabaseReference questionSettingsRef = FirebaseDatabase.getInstance().getReference()
                .child("mindHealthQuestionSetting");

        questionSettingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataSnapshot activeQuestionConfig = findActiveConfig(snapshot);
                if (activeQuestionConfig == null) {
                    showErrorAndFinish("No active question config");
                    return;
                }

                try {
                    textView3.setText("1. " + activeQuestionConfig.child("q1").getValue(String.class));
                    textView4.setText("2. " + activeQuestionConfig.child("q2").getValue(String.class));
                    textView5.setText("3. " + activeQuestionConfig.child("q3").getValue(String.class));
                } catch (Exception e) {
                    showErrorAndFinish("Error loading questions");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showErrorAndFinish("Question config error: " + error.getMessage());
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

    private void setupSubmitButton() {
        Button button6 = findViewById(R.id.button6);
        button6.setOnClickListener(v -> {
            if (validateSelections()) {
                ArrayList<String> inputs2 = appendInputs();
                Intent intent = new Intent(this, Student_test4Activity.class);
                intent.putStringArrayListExtra("inputs", textInputs);
                intent.putStringArrayListExtra("inputs2", inputs2);
                startActivity(intent);
            }
        });
    }

    private ArrayList<String> appendInputs() {
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add(String.valueOf(spinner1.getSelectedItemPosition() - 1));
        inputs.add(String.valueOf(spinner2.getSelectedItemPosition() - 1));
        inputs.add(String.valueOf(spinner3.getSelectedItemPosition() - 1));
        return inputs;
    }

    private boolean validateSelections() {
        boolean isValid = true;

        if (spinner1.getSelectedItemPosition() == 0) {
            showToast("Please select an option for question 1");
            isValid = false;
        }
        if (spinner2.getSelectedItemPosition() == 0) {
            showToast("Please select an option for question 2");
            isValid = false;
        }
        if (spinner3.getSelectedItemPosition() == 0) {
            showToast("Please select an option for question 3");
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