package com.example.mindease;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class User_Graph_ResultActivity extends AppCompatActivity {
    private ArrayList<String> allInputs = new ArrayList<>();
    private static final String TAG = "GraphActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_graph_result);

        // Initialize charts
        LineChart chartSet1 = findViewById(R.id.lineChartSet1);
        LineChart chartSet2 = findViewById(R.id.lineChartSet2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get all inputs from intent
        ArrayList<String> combinedInputs = getIntent().getStringArrayListExtra("allInputs");
        if (combinedInputs != null) {
            allInputs = combinedInputs;
            Log.d(TAG, "Received inputs: " + allInputs.toString());
        }

        // Split the data into two sets
        ArrayList<String> set1 = new ArrayList<>();
        ArrayList<String> set2 = new ArrayList<>();

        if (allInputs.size() >= 16) {
            // First 7 items (0-6) for set1 (Q1-Q7)
            set1 = new ArrayList<>(allInputs.subList(0, 7));
            // Next 9 items (7-15) for set2 (Q8-Q16)
            set2 = new ArrayList<>(allInputs.subList(7, 16));

            Log.d(TAG, "Set1 (Q1-Q7): " + set1);
            Log.d(TAG, "Set2 (Q8-Q16): " + set2);
        } else {
            Log.e(TAG, "Insufficient input data. Expected 16 items, got: " + allInputs.size());
        }

        // Process data for charts
        ArrayList<Float> valuesSet1 = processData(set1, 7);
        ArrayList<Float> valuesSet2 = processData(set2, 9);

        // Setup charts separately
        setupChart(chartSet1, valuesSet1, "Anxiety Assessment (Q1-Q7)", Color.parseColor("#BA68C8"));
        setupChart(chartSet2, valuesSet2, "Depression Assessment (Q8-Q16)", Color.parseColor("#4DB6AC"));

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(v -> {
            Intent intent = new Intent(User_Graph_ResultActivity.this, Dashboard_StudentActivity.class);
            startActivity(intent);
        });
    }

    private ArrayList<Float> processData(ArrayList<String> rawData, int expectedSize) {
        ArrayList<Float> processed = new ArrayList<>();
        if (rawData == null) return processed;

        for (int i = 0; i < expectedSize; i++) {
            try {
                float value = i < rawData.size() ? Float.parseFloat(rawData.get(i)) : 0f;
                processed.add(value);
            } catch (NumberFormatException e) {
                processed.add(0f);
                Log.e(TAG, "Invalid data at position " + i);
            }
        }
        return processed;
    }

    private void setupChart(LineChart chart, ArrayList<Float> values, String label, int color) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        // Create entries and labels
        for (int i = 0; i < values.size(); i++) {
            entries.add(new Entry(i, values.get(i)));
            labels.add("Q" + (i + 1));
        }

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(5f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.setBackgroundColor(Color.BLACK);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        // Configure X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);

        // Configure Y-axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(4f); // Increased to 4 to accommodate value 4
        leftAxis.setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);

        chart.invalidate();
    }
}