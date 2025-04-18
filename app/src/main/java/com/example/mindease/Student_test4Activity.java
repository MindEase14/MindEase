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

public class Student_test4Activity extends AppCompatActivity {
    private ArrayList<String> textInputs = new ArrayList<>();
    private ArrayList<String> textInputs2ndSetQuestion = new ArrayList<>();
    private Spinner spinner4, spinner5, spinner6;
    private TextView textView23, textView24, textView25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_test4);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupSpinners();
        setupFirebase();

        // Retrieve data passed from previous activities
        Intent intent = getIntent();
        if (intent != null) {
            textInputs = intent.getStringArrayListExtra("inputs");
            textInputs2ndSetQuestion = intent.getStringArrayListExtra("inputs2");

            if (textInputs == null) textInputs = new ArrayList<>();
            if (textInputs2ndSetQuestion == null) textInputs2ndSetQuestion = new ArrayList<>();
        }

        setupSubmitButton();
    }

    private void initializeViews() {
        spinner4 = findViewById(R.id.spinner4);
        spinner5 = findViewById(R.id.spinner5);
        spinner6 = findViewById(R.id.spinner6);

        textView23 = findViewById(R.id.textView23);
        textView24 = findViewById(R.id.textView24);
        textView25 = findViewById(R.id.textView25);
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
        spinner4.setAdapter(adapter);
        spinner5.setAdapter(adapter);
        spinner6.setAdapter(adapter);
    }

    private void setupFirebase() {
        DatabaseReference questionRef = FirebaseDatabase.getInstance().getReference()
                .child("mindHealthQuestionSetting");

        questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataSnapshot activeConfig = findActiveConfig(snapshot);
                if (activeConfig == null) {
                    showErrorAndFinish("No active question configuration");
                    return;
                }

                try {
                    textView23.setText("4. " + activeConfig.child("q4").getValue(String.class));
                    textView24.setText("5. " + activeConfig.child("q5").getValue(String.class));
                    textView25.setText("6. " + activeConfig.child("q6").getValue(String.class));
                } catch (Exception e) {
                    showErrorAndFinish("Error loading question data");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showErrorAndFinish("Database error: " + error.getMessage());
            }
        });
    }

    private void setupSubmitButton() {
        Button button7 = findViewById(R.id.button7);
        button7.setOnClickListener(v -> {
            if (validateSelections()) {
                ArrayList<String> newInputs = appendInputs();
                textInputs2ndSetQuestion.addAll(newInputs);
                Intent nextIntent = new Intent(Student_test4Activity.this, Student_Test5Activity.class);
                nextIntent.putStringArrayListExtra("inputs", textInputs);
                nextIntent.putStringArrayListExtra("inputs2", textInputs2ndSetQuestion);
                startActivity(nextIntent);
            }
        });
    }

    private ArrayList<String> appendInputs() {
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add(String.valueOf(spinner4.getSelectedItemPosition() - 1));
        inputs.add(String.valueOf(spinner5.getSelectedItemPosition() - 1));
        inputs.add(String.valueOf(spinner6.getSelectedItemPosition() - 1));
        return inputs;
    }

    private boolean validateSelections() {
        boolean isValid = true;

        if (spinner4.getSelectedItemPosition() == 0) {
            showToast("Please select an option for question 4");
            isValid = false;
        }
        if (spinner5.getSelectedItemPosition() == 0) {
            showToast("Please select an option for question 5");
            isValid = false;
        }
        if (spinner6.getSelectedItemPosition() == 0) {
            showToast("Please select an option for question 6");
            isValid = false;
        }
        return isValid;
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

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}