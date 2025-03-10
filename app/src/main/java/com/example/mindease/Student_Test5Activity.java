package com.example.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Student_Test5Activity extends AppCompatActivity {
    ArrayList<String> textInputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_test5); // Set the layout first

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        // Initialize the Button after setContentView()
        Button submit = findViewById(R.id.button9);

        // Set the click listener for the Button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if textInputs is null or empty
                if (textInputs == null || textInputs.isEmpty()) {
                    Toast.makeText(Student_Test5Activity.this, "No data to process!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert ArrayList<String> to String[]
                String[] textBoxes = textInputs.toArray(new String[0]);

                // Perform fuzzy clustering
                String result = performFuzzyClustering(textBoxes);

                // Display the result
                Toast.makeText(Student_Test5Activity.this, result, Toast.LENGTH_LONG).show();
                Log.d("FuzzyClusteringResult", result);
            }
        });
    }

    private String performFuzzyClustering(String[] textBoxes) {
        try {
            double[] responses = new double[textBoxes.length];
            for (int i = 0; i < textBoxes.length; i++) {
                responses[i] = parseResponse(textBoxes[i]);
            }

            int numClusters = 4; // Minimal, Mild, Moderate, Severe
            double[][] memberships = initializeMembershipMatrix(responses.length, numClusters);
            double[] centroids = {0.5, 1.5, 2.5, 3.0};

            performFCM(responses, centroids, memberships, numClusters);

            StringBuilder result = new StringBuilder();
            result.append("Anxiety Assessment Results:\n");

            int[] clusters = new int[responses.length];

            for (int i = 0; i < responses.length; i++) {
                int cluster = getCluster(memberships[i]);
                clusters[i] = cluster; // Store cluster for overall calculation
                String category = clusterToCategory(cluster);
                result.append("Response: " + responses[i] + " → " + category + "\n");
            }

            String overallConclusion = getOverallClusterConclusion(clusters);
            result.append(overallConclusion);

            return result.toString();
        } catch (Exception ex) {
            return "An error occurred in performFuzzyClustering(): " + ex.getMessage();
        }
    }

    private double parseResponse(String input) throws NumberFormatException {
        int response = Integer.parseInt(input);
        if (response >= 0 && response <= 3) {
            return response;
        }
        throw new NumberFormatException("Invalid input. Please enter a number between 0 and 3.");
    }

    private double[][] initializeMembershipMatrix(int numDataPoints, int numClusters) {
        Random rand = new Random(42);
        double[][] memberships = new double[numDataPoints][numClusters];

        for (int i = 0; i < numDataPoints; i++) {
            double sum = 0;

            for (int j = 0; j < numClusters; j++) {
                memberships[i][j] = rand.nextDouble();
                sum += memberships[i][j];
            }

            for (int j = 0; j < numClusters; j++) {
                memberships[i][j] /= sum;  // Normalize memberships
            }
        }

        return memberships;
    }

    private void performFCM(double[] responses, double[] centroids, double[][] memberships, int numClusters) {
        if (allResponsesEqual(responses)) {
            int highestCluster = responses[0] == 3 ? 3 : 0;

            for (int i = 0; i < memberships.length; i++) {
                for (int j = 0; j < numClusters; j++) {
                    memberships[i][j] = (j == highestCluster) ? 1 : 0;
                }
            }
            return;
        }

        int maxIterations = 200;  // Increased iteration count
        double epsilon = 0.01;    // Increased tolerance

        for (int iter = 0; iter < maxIterations; iter++) {
            double[] previousCentroids = centroids.clone();

            // Update centroids
            for (int j = 0; j < numClusters; j++) {
                double numerator = 0, denominator = 0;

                for (int i = 0; i < responses.length; i++) {
                    double membership = Math.pow(memberships[i][j], 2);
                    numerator += membership * responses[i];
                    denominator += membership;
                }

                centroids[j] = denominator == 0 ? centroids[j] : numerator / denominator;
            }

            // Check convergence
            if (maxDifference(centroids, previousCentroids) < epsilon) {
                break;
            }

            // Update memberships
            for (int i = 0; i < responses.length; i++) {
                for (int j = 0; j < numClusters; j++) {
                    double sum = 0;
                    for (int k = 0; k < numClusters; k++) {
                        double ratio = (Math.abs(responses[i] - centroids[j]) + epsilon)
                                / (Math.abs(responses[i] - centroids[k]) + epsilon);
                        sum += Math.pow(ratio, 2);
                    }
                    memberships[i][j] = 1.0 / sum;
                }
            }
        }
    }

    private int getCluster(double[] membershipValues) {
        int maxIndex = 0;
        double maxValue = membershipValues[0];
        for (int i = 1; i < membershipValues.length; i++) {
            if (membershipValues[i] > maxValue) {
                maxValue = membershipValues[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private String clusterToCategory(int cluster) {
        switch (cluster) {
            case 0:
                return "Minimal Anxiety - Continue with your regular routine, but consider practicing stress-reduction strategies occasionally.";
            case 1:
                return "Mild Anxiety - Try engaging in self-care practices such as exercise, meditation, or talking to someone about your feelings.";
            case 2:
                return "Moderate Anxiety - Consider practicing relaxation techniques, mindfulness, or seeking professional guidance to manage anxiety.";
            case 3:
                return "Severe Anxiety - It is highly recommended to seek professional help, such as therapy or counseling, to address severe anxiety symptoms.";
            default:
                return "Please seek professional help if needed.";
        }
    }

    private String getOverallClusterConclusion(int[] clusters) {
        // Step 1: Count occurrences of each cluster
        HashMap<Integer, Integer> clusterCounts = new HashMap<>();
        for (int cluster : clusters) {
            clusterCounts.put(cluster, clusterCounts.getOrDefault(cluster, 0) + 1);
        }

        // Step 2: Find the cluster with the highest count
        int maxCluster = -1;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : clusterCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCluster = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        // Step 3: Map the cluster to its corresponding category
        String category = clusterToCategory(maxCluster);

        // Step 4: Return the formatted result
        return "\n" + category;
    }

    private boolean allResponsesEqual(double[] responses) {
        double first = responses[0];
        for (double response : responses) {
            if (response != first) {
                return false;
            }
        }
        return true;
    }

    private double maxDifference(double[] a, double[] b) {
        double maxDiff = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = Math.abs(a[i] - b[i]);
            if (diff > maxDiff) {
                maxDiff = diff;
            }
        }
        return maxDiff;
    }
}