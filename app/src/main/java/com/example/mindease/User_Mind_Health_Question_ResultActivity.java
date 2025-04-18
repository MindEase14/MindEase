package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class User_Mind_Health_Question_ResultActivity extends AppCompatActivity {
    private ArrayList<String> allInputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_mind_health_question_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // all the inputs are being received in this block
        ArrayList<String> combinedInputs = getIntent().getStringArrayListExtra("allInputs");
        if (combinedInputs != null) {
            allInputs = combinedInputs;
        }

        Button button1 = findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Mind_Health_Question_ResultActivity.this, User_Graph_ResultActivity.class);
                intent.putStringArrayListExtra("allInputs", allInputs); // Pass combined inputs
                startActivity(intent);
            }
        });

    }
}