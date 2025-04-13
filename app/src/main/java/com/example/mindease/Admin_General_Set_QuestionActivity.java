package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Admin_General_Set_QuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_general_set_question);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Add click listener here
        View view = findViewById(R.id.view);
        view.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_General_Set_QuestionActivity.this, Admin_General_Anxiety_QuestionierActivity.class);
            startActivity(intent);
        });

        // Add click listener here
        View view2 = findViewById(R.id.view2);
        view2.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_General_Set_QuestionActivity.this, Admin_Mind_Health_QuestionnaireActivity.class);
            startActivity(intent);
        });
        View view3 = findViewById(R.id.view3);
        view3.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_General_Set_QuestionActivity.this, Admin_Questionnaire_SettingActivity.class);
            startActivity(intent);
        });
    }
}
