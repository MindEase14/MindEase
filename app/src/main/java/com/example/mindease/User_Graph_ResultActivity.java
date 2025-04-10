package com.example.mindease;

import android.graphics.Color;
import android.os.Bundle;

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

        LineChart lineChart;
        lineChart = findViewById(R.id.lineChart);

        // Sample data
        int[] values = {1, 2, 1, 3, 1, 2, 1, 1};
        String[] labels = {"q1", "q2", "q3", "q4", "q5", "q6", "q7", "q8"};

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            entries.add(new Entry(i, values[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Result");
        dataSet.setColor(Color.parseColor("#BA68C8"));
        dataSet.setCircleColor(Color.parseColor("#BA68C8"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(Color.WHITE);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.setBackgroundColor(Color.BLACK);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(4f);
        leftAxis.setTextColor(Color.WHITE);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.invalidate(); // Refresh chart
    }
}
