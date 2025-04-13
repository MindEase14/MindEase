package com.example.mindease;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Admin_Questionnaire_SettingActivity extends AppCompatActivity {

    Spinner spinnerTest, spinnerGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_questionnaire_setting); // your XML file name

        spinnerTest = findViewById(R.id.spinnerTest);
        spinnerGroup = findViewById(R.id.spinnerGroup);

        // Example data
        String[] tests = {"Select Test", "General Anxiety Test", "Mind Health Test",};
        String[] groups = {"Select Group", "1", "2", "3"};

        ArrayAdapter<String> testAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, tests);
        testAdapter.setDropDownViewResource(R.layout.spinner_item); // Use same custom layout for dropdown
        spinnerTest.setAdapter(testAdapter);

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, groups);
        groupAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerGroup.setAdapter(groupAdapter);

        // Optional: Add listener
        spinnerTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedTest = adapterView.getItemAtPosition(position).toString();
                if (!selectedTest.equals("Select Test")) {
                    Toast.makeText(Admin_Questionnaire_SettingActivity.this, "Selected Test: " + selectedTest, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedGroup = adapterView.getItemAtPosition(position).toString();
                if (!selectedGroup.equals("Select Group")) {
                    Toast.makeText(Admin_Questionnaire_SettingActivity.this, "Selected Group: " + selectedGroup, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }
}