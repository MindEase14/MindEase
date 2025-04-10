package com.example.mindease;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Admin_Diagnostic_SettingActivity extends AppCompatActivity {

    private Spinner spinnerTest, spinnerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_diagnostic_setting);

        // Initialize Spinners
        spinnerTest = findViewById(R.id.spinnerTest);
        spinnerName = findViewById(R.id.spinnerName);

        // Setup adapters with hints
        setupSpinnerAdapters();

        // Window insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupSpinnerAdapters() {
        // For Test spinner
        List<CharSequence> testItems = new ArrayList<>();
        testItems.add("Select Test"); // Hint
        testItems.addAll(Arrays.asList(getResources().getStringArray(R.array.test_options)));

        ArrayAdapter<CharSequence> testAdapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_item,
                testItems
        );
        testAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinnerTest.setAdapter(testAdapter);
        spinnerTest.setSelection(0, false); // Show hint initially

        // For Name spinner
        List<CharSequence> nameItems = new ArrayList<>();
        nameItems.add("Select Name"); // Hint
        nameItems.addAll(Arrays.asList(getResources().getStringArray(R.array.name_options)));

        ArrayAdapter<CharSequence> nameAdapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_item,
                nameItems
        );
        nameAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinnerName.setAdapter(nameAdapter);
        spinnerName.setSelection(0, false); // Show hint initially

        // Handle selections
        setupSelectionListeners();
    }

    private void setupSelectionListeners() {
        spinnerTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Hint selected, do nothing
                    return;
                }
                String selected = parent.getItemAtPosition(position).toString();
                // Handle test selection
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Hint selected, do nothing
                    return;
                }
                String selected = parent.getItemAtPosition(position).toString();
                // Handle name selection
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}