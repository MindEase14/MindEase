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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Student_Test3Activity extends AppCompatActivity {
    ArrayList<String> textInputs = new ArrayList<>();
    private EditText editTextText1, editTextText2, editTextText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call super.onCreate() first
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_test3); // Set the layout

        // Initialize EditText fields after setContentView()
        editTextText1 = findViewById(R.id.editTextText1);
        editTextText2 = findViewById(R.id.editTextText2);
        editTextText3 = findViewById(R.id.editTextText3);

        // Add TextWatchers to each EditText
        addTextWatcher(editTextText1);
        addTextWatcher(editTextText2);
        addTextWatcher(editTextText3);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the data passed from the previous activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("inputs")) {
            textInputs = intent.getStringArrayListExtra("inputs");

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

        Button button6 = findViewById(R.id.button6); // Find button by its ID

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all inputs are valid before proceeding
                if (validateInput(editTextText1) && validateInput(editTextText2) && validateInput(editTextText3)) {
                    // Get the new inputs
                    ArrayList<String> inputs2 = appendInputs();

                    // Create an Intent to start the next activity
                    Intent intent = new Intent(Student_Test3Activity.this, Student_test4Activity.class);
                    intent.putStringArrayListExtra("inputs", textInputs);  // Pass the original inputs
                    intent.putStringArrayListExtra("inputs2", inputs2);   // Pass the new inputs
                    startActivity(intent);
                } else {
                    Toast.makeText(Student_Test3Activity.this, "Please correct the inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<String> appendInputs() {
        ArrayList<String> inputs = new ArrayList<>(); // Create a new array to store the inputs
        inputs.add(editTextText1.getText().toString());
        inputs.add(editTextText2.getText().toString());
        inputs.add(editTextText3.getText().toString());
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