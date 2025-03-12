package com.example.mindease;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Student_Test5Activity extends AppCompatActivity {
    ArrayList<String> textInputs = new ArrayList<>();
    ArrayList<String> textInputs2ndSetQuestion = new ArrayList<>();
    private EditText editTextText11, editTextText12, editTextText13;
    String uid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_test5); // Set the layout first

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize EditText fields after setContentView()
        editTextText11 = findViewById(R.id.editTextText11);
        editTextText12 = findViewById(R.id.editTextText12);
        editTextText13 = findViewById(R.id.editTextText13);

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

        // Retrieve the second set of questions data passed from the previous activity
        Intent intent2 = getIntent();
        if (intent2 != null && intent2.hasExtra("inputs2")) {
            textInputs2ndSetQuestion = intent2.getStringArrayListExtra("inputs2");

            // Check if textInputs is null before using it
            if (textInputs2ndSetQuestion != null) {
                // Log the inputs
                for (int i = 0; i < textInputs2ndSetQuestion.size(); i++) {
                    Log.d("TextInput2", "Input " + i + ": " + textInputs2ndSetQuestion.get(i));
                }
            } else {
                Toast.makeText(this, "No data received for second set questions!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where no data is passed
            Toast.makeText(this, "No data received from second set question!", Toast.LENGTH_SHORT).show();
        }

        uid = FirebaseUtils.getCurrentUserUID();
        if (uid != null) {
            Log.d("User", "Current userID logged in: " + uid);

        } else {
            Log.d("User", "No user logged in");
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

                appendInputs();
                // Convert ArrayList<String> to String[]
                String[] textBoxes = textInputs.toArray(new String[0]);
                String[] textBoxes2 = textInputs2ndSetQuestion.toArray(new String[0]);

                // Perform fuzzy clustering for both sets of questions and get the final evaluation
                String finalEvaluation1 = getFinalEvaluation(textBoxes);
                String finalEvaluation2 = getFinalEvaluation(textBoxes2);

                // Combine the final evaluations
                String combinedResult = "Final Evaluation for 1st set:\n" + finalEvaluation1 + "\n\n" +
                        "Final Evaluation for 2nd set:\n" + finalEvaluation2;

                // Display the final evaluation in a popup dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Student_Test5Activity.this);
                builder.setTitle("Final Evaluation");
                builder.setMessage(combinedResult);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                // Log the final evaluations
                Log.d("FinalEvaluation for 1st set:", finalEvaluation1);
                Log.d("FinalEvaluation for 2nd set:", finalEvaluation2);

                // Store the results in Firebase Realtime Database
                storeResultsInFirebase(finalEvaluation1, finalEvaluation2);
            }
        });
    }

    private String getFinalEvaluation(String[] textBoxes) {
        try {
            double[] responses = new double[textBoxes.length];
            for (int i = 0; i < textBoxes.length; i++) {
                responses[i] = parseResponse(textBoxes[i]);
            }

            int numClusters = 4; // Minimal, Mild, Moderate, Severe
            double[][] memberships = initializeMembershipMatrix(responses.length, numClusters);
            double[] centroids = {0.5, 1.5, 2.5, 3.0};

            performFCM(responses, centroids, memberships, numClusters);

            int[] clusters = new int[responses.length];
            for (int i = 0; i < responses.length; i++) {
                clusters[i] = getCluster(memberships[i]);
            }

            // Get the overall conclusion
            return getOverallClusterConclusion(clusters);
        } catch (Exception ex) {
            return "An error occurred in getFinalEvaluation(): " + ex.getMessage();
        }
    }

    private void appendInputs() {
        textInputs2ndSetQuestion.add(editTextText11.getText().toString());
        textInputs2ndSetQuestion.add(editTextText12.getText().toString());
        textInputs2ndSetQuestion.add(editTextText13.getText().toString());
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

    // Function to store results in Firebase Realtime Database
    private void storeResultsInFirebase(String finalEvaluation1, String finalEvaluation2) {
        // Get a reference to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recordsRef = database.getReference("records/"+uid+"/");

        // Create a unique key for the record
        String recordId = recordsRef.push().getKey();

        // Create a map to store the results
        Map<String, Object> recordMap = new HashMap<>();

        // Store the first set of inputs
        for (int i = 0; i < textInputs.size(); i++) {
            recordMap.put("set1_question" + (i + 1), textInputs.get(i)); // Use get(i) instead of indexOf(i)
        }

        // Store the second set of inputs
        for (int i = 0; i < textInputs2ndSetQuestion.size(); i++) {
            recordMap.put("set2_question" + (i + 1), textInputs2ndSetQuestion.get(i)); // Use get(i) instead of indexOf(i)
        }

        // Store the final evaluations and timestamp
        recordMap.put("finalEvaluation1", finalEvaluation1);
        recordMap.put("finalEvaluation2", finalEvaluation2);
        recordMap.put("timestamp", System.currentTimeMillis()); // Add a timestamp

        // Store the results in the database
        if (recordId != null) {
            recordsRef.child(recordId).setValue(recordMap)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Results stored successfully!");
                        Toast.makeText(Student_Test5Activity.this, "Results saved to database!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Failed to store results: " + e.getMessage());
                        Toast.makeText(Student_Test5Activity.this, "Failed to save results!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}