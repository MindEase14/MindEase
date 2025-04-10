package com.example.mindease;

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

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;


public class Admin_Mind_Health_SettingActivity extends AppCompatActivity {
    private EditText setname, minimal, mild, moderate, severe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_mind_health_setting);

        // Window insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize EditText fields
        setname = findViewById(R.id.editTextText1);
        mild = findViewById(R.id.editTextText2);
        minimal = findViewById(R.id.editTextText3);
        moderate = findViewById(R.id.editTextText4);
        severe = findViewById(R.id.editTextText15);

        Button submit = findViewById(R.id.button);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    String name = setname.getText().toString().trim();
                    String minimalText = minimal.getText().toString().trim();
                    String mildText = mild.getText().toString().trim();
                    String moderateText = moderate.getText().toString().trim();
                    String severeText = severe.getText().toString().trim();

// Reference to database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("mindHealthTestSetting").child(name);

// Create a map to hold the data
                    Map<String, Object> data = new HashMap<>();
                    data.put("0", minimalText);
                    data.put("1", mildText);
                    data.put("2", moderateText);
                    data.put("3", severeText);
                    data.put("status", false); // default value

// Push data to Firebase
                    ref.setValue(data).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Admin_Mind_Health_SettingActivity.this,
                                    "Data saved successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Admin_Mind_Health_SettingActivity.this,
                                    "Failed to save data.", Toast.LENGTH_SHORT).show();
                        }
                        clearField(setname);
                        clearField(minimal);
                        clearField(mild);
                        clearField(moderate);
                        clearField(severe);
                    });

                }
            }
        });
    }

    private void clearField(EditText textfield){
        textfield.setText("");
    }

    // Function to validate all input fields
    private boolean validateInputs() {
        boolean isValid = true;

        if (setname.getText().toString().trim().isEmpty()) {
            setname.setError("Set name cannot be empty");
            isValid = false;
        }

        if (minimal.getText().toString().trim().isEmpty()) {
            minimal.setError("Minimal value cannot be empty");
            isValid = false;
        }

        if (mild.getText().toString().trim().isEmpty()) {
            mild.setError("Mild value cannot be empty");
            isValid = false;
        }

        if (moderate.getText().toString().trim().isEmpty()) {
            moderate.setError("Moderate value cannot be empty");
            isValid = false;
        }

        if (severe.getText().toString().trim().isEmpty()) {
            severe.setError("Severe value cannot be empty");
            isValid = false;
        }

        return isValid;
    }
}