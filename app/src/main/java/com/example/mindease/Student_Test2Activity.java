package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Student_Test2Activity extends AppCompatActivity {
    ArrayList<String> inputs = new ArrayList<>();
    private EditText editTextText5, editTextText6, editTextText7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call super.onCreate() first
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_test2); // Set the layout

        // Initialize EditText fields after setContentView()
        editTextText5 = findViewById(R.id.editTextText5);
        editTextText6 = findViewById(R.id.editTextText6);
        editTextText7 = findViewById(R.id.editTextText7);

        // Retrieve the data passed from the previous activity
        Intent intent = getIntent();
        ArrayList<String> textInputs = new ArrayList<>();
        if (intent != null && intent.hasExtra("inputs")) {
            textInputs = intent.getStringArrayListExtra("inputs");
            inputs = textInputs;

            // Check if textInputs is null before using it
            if (textInputs != null) {
                // Log the inputs
                for (int i = 0; i < textInputs.size(); i++) {
                    Log.d("TextInput", "Input " + i + ": " + textInputs.get(i));
                }
            } else {
                Toast.makeText(this, "No data received!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where no data is passed
            Toast.makeText(this, "No data received!", Toast.LENGTH_SHORT).show();
        }

        Button button5 = findViewById(R.id.button5); // Find button by its ID

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if any input box is empty
                if (editTextText5.getText().toString().trim().isEmpty() ||
                        editTextText6.getText().toString().trim().isEmpty() ||
                        editTextText7.getText().toString().trim().isEmpty()) {
                    Toast.makeText(Student_Test2Activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Append inputs and start the next activity
                ArrayList<String> inputs = appendInputs();
                Intent intent = new Intent(Student_Test2Activity.this, Student_Test3Activity.class);
                intent.putStringArrayListExtra("inputs", inputs); // Pass the ArrayList to the next activity
                startActivity(intent);
            }
        });
    }

    private ArrayList<String> appendInputs() {
        inputs.add(editTextText5.getText().toString());
        inputs.add(editTextText6.getText().toString());
        inputs.add(editTextText7.getText().toString());
        return inputs;
    }
}