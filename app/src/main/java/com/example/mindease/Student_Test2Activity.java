package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        // Add TextWatchers to each EditText
        addTextWatcher(editTextText5);
        addTextWatcher(editTextText6);
        addTextWatcher(editTextText7);

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
                // Check if all inputs are valid before proceeding
                if (validateInput(editTextText5) && validateInput(editTextText6) && validateInput(editTextText7)) {
                    // If all inputs are valid, append inputs and start the next activity
                    ArrayList<String> inputs = appendInputs();
                    Intent intent = new Intent(Student_Test2Activity.this, Student_Test3Activity.class);
                    intent.putStringArrayListExtra("inputs", inputs); // Pass the ArrayList to the next activity
                    startActivity(intent);
                } else {
                    Toast.makeText(Student_Test2Activity.this, "Please correct the inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<String> appendInputs() {
        inputs.add(editTextText5.getText().toString());
        inputs.add(editTextText6.getText().toString());
        inputs.add(editTextText7.getText().toString());
        return inputs;
    }

    private boolean validateInput(EditText inputBox) {
        String input = inputBox.getText().toString().trim();

        // Check if the input is empty
        if (input.isEmpty()) {
            inputBox.setError("Field cannot be empty");
            return false;
        }

        // Check if the input is a number
        try {
            int number = Integer.parseInt(input);

            // Check if the number is between 0 and 3 (inclusive)
            if (number < 0 || number > 3) {
                inputBox.setError("Input must be 0, 1, 2, or 3");
                return false;
            }
        } catch (NumberFormatException e) {
            // If the input is not a number, show an error
            inputBox.setError("Input must be a number");
            return false;
        }

        // Clear any previous error
        inputBox.setError(null);
        return true; // Input is valid
    }

    private void addTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate input on every key press
                validateInput(editText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }
}