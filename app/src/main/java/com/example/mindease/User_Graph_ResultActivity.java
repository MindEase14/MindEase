package com.example.mindease;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

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
    private static final String TAG = "GraphActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_graph_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize both line charts
        LineChart lineChart1 = findViewById(R.id.lineChart1);
        LineChart lineChart2 = findViewById(R.id.lineChart2);

        // Get data from previous activity
        ArrayList<String> set1 = getIntent().getStringArrayListExtra("set1");
        ArrayList<String> set2 = getIntent().getStringArrayListExtra("set2");

        // Configure both charts
        setupChart(lineChart1, processData(set1, 7), "Anxiety Assessment (Q1-Q7)", Color.parseColor("#BA68C8"));
        setupChart(lineChart2, processData(set2, 9), "Mental Health Assessment (Q1-Q9)", Color.parseColor("#4CAF50"));
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
        leftAxis.setAxisMaximum(3f);
        leftAxis.setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);

        chart.invalidate();
    }
}