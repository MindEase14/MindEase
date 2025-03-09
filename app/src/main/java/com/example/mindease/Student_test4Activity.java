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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Student_test4Activity extends AppCompatActivity {
    ArrayList<String> textInputs = new ArrayList<>();
    ArrayList<String> textInputs2ndSetQuestion = new ArrayList<>();
    private EditText editTextText8, editTextText9, editTextText10;


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

        // Initialize EditText fields after setContentView()
        editTextText8 = findViewById(R.id.editTextText8);
        editTextText9 = findViewById(R.id.editTextText9);
        editTextText10 = findViewById(R.id.editTextText10);

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

        // Retrieve the second set of questions data passed from the previous activity
        Intent intent2 = getIntent();
        if (intent2 != null && intent2.hasExtra("inputs2")) {
            textInputs2ndSetQuestion = intent2.getStringArrayListExtra("inputs2");

            // Check if textInputs is null before using it
            if (textInputs2ndSetQuestion != null) {
                // Log the inputs
                for (int i = 0; i < textInputs2ndSetQuestion.size(); i++) {
                    Log.d("TextInput2", "Input " + i + ": " + textInputs2ndSetQuestion.get(i));
                }
            } else {
                Toast.makeText(this, "No data received for second set questions!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where no data is passed
            Toast.makeText(this, "No data received from second set question!", Toast.LENGTH_SHORT).show();
        }

        Button button7 = findViewById(R.id.button7); // Find button by its ID

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Append inputs and start the next activity
                Intent intent = new Intent(Student_test4Activity.this, Student_Test5Activity.class);
                intent.putStringArrayListExtra("inputs", textInputs); // Pass the ArrayList to the next activity
                startActivity(intent);

                // Append second set inputs and start the next activity
                ArrayList<String> inputs2= appendInputs();
                Intent intent2 = new Intent(Student_test4Activity.this, Student_Test5Activity.class);
                intent.putExtra("inputs2", inputs2); // Pass the array to the next activity
                startActivity(intent2);
            }
        });
    }

    private ArrayList<String> appendInputs() {
        ArrayList<String> inputs = new ArrayList<>(); // Create a new array to store the inputs
        inputs.add(editTextText8.getText().toString());
        inputs.add(editTextText9.getText().toString());
        inputs.add(editTextText10.getText().toString());
        return inputs;
    }
}