package com.example.mindease;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        // Add TextWatchers to each EditText
        addTextWatcher(editTextText11);
        addTextWatcher(editTextText12);
        addTextWatcher(editTextText13);

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
                if (validateInput(editTextText11) && validateInput(editTextText12) && validateInput(editTextText13)){
                    if (textInputs == null || textInputs.isEmpty()) {
                        Toast.makeText(Student_Test5Activity.this, "No data to process!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    appendInputs();
                    String[] textBoxes = textInputs.toArray(new String[0]);
                    String[] textBoxes2 = textInputs2ndSetQuestion.toArray(new String[0]);

                    // Get numeric results
                    int finalEvaluation1 = getFinalEvaluation(textBoxes);
                    int finalEvaluation2 = getFinalEvaluation(textBoxes2);

                    // Display simple result dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(Student_Test5Activity.this);
                    builder.setTitle("Evaluation Results");
                    builder.setMessage("1st Set Result: " + finalEvaluation1 + "\n" +
                            "2nd Set Result: " + finalEvaluation2);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // Store the numeric results in Firebase
                    storeResultsInFirebase(finalEvaluation1, finalEvaluation2);

                    // Open next activity
                    Intent intent = new Intent(Student_Test5Activity.this, User_Graph_ResultActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Student_Test5Activity.this, "Please correct the inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int getFinalEvaluation(String[] textBoxes) {
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

            // Return just the numeric value
            return getOverallClusterConclusion(clusters);
        } catch (Exception ex) {
            Log.e("EvaluationError", "Error in getFinalEvaluation: " + ex.getMessage());
            return -1; // Error value
        }
    }

    private void appendInputs() {
        textInputs2ndSetQuestion.add(editTextText11.getText().toString());
        textInputs2ndSetQuestion.add(editTextText12.getText().toString());
        textInputs2ndSetQuestion.add(editTextText13.getText().toString());
    }


    private boolean validateInput(EditText inputBox) {
        String input = inputBox.getText().toString().trim();

        // Check if the input is empty
        if (input.isEmpty()) {
            inputBox.setError("Field cannot be empty");
            return false;
        }

        // Check if the input is a number
        try {
            int number = Integer.parseInt(input);

            // Check if the number is between 0 and 3 (inclusive)
            if (number < 0 || number > 3) {
                inputBox.setError("Input must be 0, 1, 2, or 3");
                return false;
            }
        } catch (NumberFormatException e) {
            // If the input is not a number, show an error
            inputBox.setError("Input must be a number");
            return false;
        }

        // Clear any previous error
        inputBox.setError(null);
        return true; // Input is valid
    }

    private void addTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate input on every key press
                validateInput(editText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
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

    private int clusterToCategory(int cluster) {
        // Just return the numeric value (1-4)
        return cluster + 1; // Since clusters are 0-3, add 1 to make them 1-4
    }

    private int getOverallClusterConclusion(int[] clusters) {
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

        // Step 3: Return the numeric value (1-4)
        return maxCluster + 1;
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
    private void storeResultsInFirebase(int finalEvaluation1, int finalEvaluation2) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recordsRef = database.getReference("records/" + uid + "/");

        String recordId = recordsRef.push().getKey();

        Map<String, Object> recordMap = new HashMap<>();

        // Store the first set of inputs
        for (int i = 0; i < textInputs.size(); i++) {
            recordMap.put("set1_question" + (i + 1), textInputs.get(i));
        }

        // Store the second set of inputs
        for (int i = 0; i < textInputs2ndSetQuestion.size(); i++) {
            recordMap.put("set2_question" + (i + 1), textInputs2ndSetQuestion.get(i));
        }

        // Store numeric evaluations using "finalEvaluation1" and "finalEvaluation2"
        recordMap.put("finalEvaluation1", finalEvaluation1);
        recordMap.put("finalEvaluation2", finalEvaluation2);

        // Timestamp
        recordMap.put("timestamp", System.currentTimeMillis());

        if (recordId != null) {
            recordsRef.child(recordId).setValue(recordMap)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Results stored successfully!");
                        Toast.makeText(Student_Test5Activity.this, "Results saved!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Failed to store results: " + e.getMessage());
                        Toast.makeText(Student_Test5Activity.this, "Failed to save results!", Toast.LENGTH_SHORT).show();
                    });
        }
    }


}