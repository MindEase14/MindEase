package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button4 = findViewById(R.id.button4); // Find button by its ID

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if any input box is empty
                if (checkInputBoxes(editText1)) {
                    Toast.makeText(Student_TestActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                } else if (checkInputBoxes(editText2)) {
                    Toast.makeText(Student_TestActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                } else if (checkInputBoxes(editText3)) {
                    Toast.makeText(Student_TestActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                } else if (checkInputBoxes(editText4)) {
                    Toast.makeText(Student_TestActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Append inputs and start the next activity
                ArrayList<String> inputs = appendInputs();
                Intent intent = new Intent(Student_TestActivity.this, Student_Test2Activity.class);
                intent.putExtra("inputs", inputs); // Pass the array to the next activity
                startActivity(intent);
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

    private Boolean checkInputBoxes(EditText inputBox) {
        return inputBox.getText().toString().trim().isEmpty(); // Check if the input is empty or contains only whitespace
    }
}