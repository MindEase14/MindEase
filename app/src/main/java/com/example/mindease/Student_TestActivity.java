package com.example.mindease;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class Student_TestActivity extends AppCompatActivity {

    private Spinner spinner1, spinner2, spinner3, spinner4;
    private TextView textView15, textView21, textView22, textView23, textView16, textView18, textView19, textView20;
    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_test);

        // Retrieve uid from Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uid = extras.getString("uid", "");
        }

        initializeViews();
        initializeSpinners();
        setupFirebase();
        setupSubmitButton();
    }

    private void initializeViews() {
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);
        spinner4 = findViewById(R.id.spinner4);

        textView15 = findViewById(R.id.textView15);
        textView21 = findViewById(R.id.textView21);
        textView22 = findViewById(R.id.textView22);
        textView23 = findViewById(R.id.textView23);
        textView16 = findViewById(R.id.textView16);
        textView18 = findViewById(R.id.textView18);
        textView19 = findViewById(R.id.textView19);
        textView20 = findViewById(R.id.textView20);
    }

    private void initializeSpinners() {
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
        spinner4.setAdapter(adapter);
    }

    private void setupFirebase() {
        DatabaseReference testSettingsRef = FirebaseDatabase.getInstance().getReference()
                .child("generalAnxietyTestSetting");

        testSettingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataSnapshot activeLabelConfig = findActiveConfig(snapshot);

                if (activeLabelConfig == null) {
                    showErrorAndFinish("No active label configuration found");
                    return;
                }

                try {
                    textView15.setText("0 = \"" + activeLabelConfig.child("0").getValue(String.class) + "\"");
                    textView21.setText("1 = \"" + activeLabelConfig.child("1").getValue(String.class) + "\"");
                    textView22.setText("2 = \"" + activeLabelConfig.child("2").getValue(String.class) + "\"");
                    textView23.setText("3 = \"" + activeLabelConfig.child("3").getValue(String.class) + "\"");
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
                .child("generalAnxietyQuestionSetting");

        questionSettingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataSnapshot activeQuestionConfig = findActiveConfig(snapshot);

                if (activeQuestionConfig == null) {
                    showErrorAndFinish("No active question configuration found");
                    return;
                }

                try {
                    textView16.setText("1. " + activeQuestionConfig.child("q1").getValue(String.class));
                    textView18.setText("2. " + activeQuestionConfig.child("q2").getValue(String.class));
                    textView19.setText("3. " + activeQuestionConfig.child("q3").getValue(String.class));
                    textView20.setText("4. " + activeQuestionConfig.child("q4").getValue(String.class));
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
        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(v -> {
            ArrayList<String> inputs = new ArrayList<>();
            inputs.add(spinner1.getSelectedItem().toString());
            inputs.add(spinner2.getSelectedItem().toString());
            inputs.add(spinner3.getSelectedItem().toString());
            inputs.add(spinner4.getSelectedItem().toString());

            Intent intent = new Intent(this, Student_Test2Activity.class);
            intent.putStringArrayListExtra("inputs", inputs);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}