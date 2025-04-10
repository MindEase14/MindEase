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

public class Student_TestActivity extends AppCompatActivity {
    private EditText editText1, editText2, editText3, editText4;
    String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_test); // Set the layout first

        // Initialize EditText fields after setContentView()
        editText1 = findViewById(R.id.editTextText);
        editText2 = findViewById(R.id.editTextText2);
        editText3 = findViewById(R.id.editTextText3);
        editText4 = findViewById(R.id.editTextText4);

        // Add TextWatchers to each EditText
        addTextWatcher(editText1);
        addTextWatcher(editText2);
        addTextWatcher(editText3);
        addTextWatcher(editText4);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button4 = findViewById(R.id.button4); // Find button by its ID

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all inputs are valid before proceeding
                if (validateInput(editText1) && validateInput(editText2) && validateInput(editText3) && validateInput(editText4)) {
                    // If all inputs are valid, append inputs and start the next activity
                    ArrayList<String> inputs = appendInputs();
                    Intent intent = new Intent(Student_TestActivity.this, Student_Test2Activity.class);
                    intent.putExtra("inputs", inputs); // Pass the array to the next activity
                    intent.putExtra("uid", uid); // Pass the email to the next activity
                    startActivity(intent);
                } else {
                    Toast.makeText(Student_TestActivity.this, "Please correct the inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<String> appendInputs() {
        ArrayList<String> inputs = new ArrayList<>(); // Create a new array to store the inputs
        inputs.add(editText1.getText().toString());
        inputs.add(editText2.getText().toString());
        inputs.add(editText3.getText().toString());
        inputs.add(editText4.getText().toString());
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