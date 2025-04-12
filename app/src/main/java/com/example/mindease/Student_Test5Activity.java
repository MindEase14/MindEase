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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Student_Test5Activity extends AppCompatActivity {
    ArrayList<String> textInputs = new ArrayList<>();
    ArrayList<String> textInputs2ndSetQuestion = new ArrayList<>();
    private EditText editTextText11, editTextText12, editTextText13;
    String uid = "";
    private int finalEvaluation1, finalEvaluation2;

    interface FirebaseCallback {
        void onCallback(Map<Integer, String> resultMap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_test5);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextText11 = findViewById(R.id.editTextText11);
        editTextText12 = findViewById(R.id.editTextText12);
        editTextText13 = findViewById(R.id.editTextText13);

        addTextWatcher(editTextText11);
        addTextWatcher(editTextText12);
        addTextWatcher(editTextText13);

        // Retrieve first set of questions
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("inputs")) {
            textInputs = intent.getStringArrayListExtra("inputs");
            if (textInputs == null) {
                Toast.makeText(this, "No data received!", Toast.LENGTH_SHORT).show();
            }
        }

        // Retrieve second set of questions
        if (intent != null && intent.hasExtra("inputs2")) {
            textInputs2ndSetQuestion = intent.getStringArrayListExtra("inputs2");
            if (textInputs2ndSetQuestion == null) {
                Toast.makeText(this, "No second set data!", Toast.LENGTH_SHORT).show();
            }
        }

        uid = FirebaseUtils.getCurrentUserUID();
        if (uid == null) Log.d("User", "No user logged in");

        Button submit = findViewById(R.id.button9);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput(editTextText11) && validateInput(editTextText12) && validateInput(editTextText13)) {
                    if (textInputs == null || textInputs.isEmpty()) {
                        Toast.makeText(Student_Test5Activity.this, "No data to process!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    appendInputs();
                    String[] textBoxes = textInputs.toArray(new String[0]);
                    String[] textBoxes2 = textInputs2ndSetQuestion.toArray(new String[0]);

                    finalEvaluation1 = getFinalEvaluation(textBoxes);
                    finalEvaluation2 = getFinalEvaluation(textBoxes2);

                    fetchAndDisplayResults();
                } else {
                    Toast.makeText(Student_Test5Activity.this, "Please correct the inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void fetchAndDisplayResults() {
        final StringBuilder message = new StringBuilder();

        fetchGeneralAnxietyTestSetting(new FirebaseCallback() {
            @Override
            public void onCallback(Map<Integer, String> anxietyMap) {
                fetchMindHealthTestSetting(new FirebaseCallback() {
                    @Override
                    public void onCallback(Map<Integer, String> mindHealthMap) {
                        // Get evaluation strings using cluster results
                        String eval1Str = "N/A";
                        String eval2Str = "N/A";

                        if (anxietyMap != null && finalEvaluation1 >= 1 && finalEvaluation1 <= 4) {
                            eval1Str = anxietyMap.get(finalEvaluation1 - 1); // Adjust index
                        }
                        if (mindHealthMap != null && finalEvaluation2 >= 1 && finalEvaluation2 <= 4) {
                            eval2Str = mindHealthMap.get(finalEvaluation2 - 1); // Adjust index
                        }

                        message.append("Final Evaluation 1 : ").append(eval1Str != null ? eval1Str : "N/A").append("\n");
                        message.append("Final Evaluation 2 : ").append(eval2Str != null ? eval2Str : "N/A").append("\n\n");

                        // Swap sets: Set 1 = Mental Health, Set 2 = Anxiety
                        message.append("Set 1 (Anxiety)\n");
                        for (int i = 0; i < textInputs.size(); i++) {
                            message.append("Question ").append(i + 1).append(" : ").append(textInputs.get(i)).append("\n");
                        }

                        message.append("\nSet 2 (Mental Health)\n");
                        for (int i = 0; i < 9; i++) {
                            message.append("Question ").append(i + 1).append(" : ").append(textInputs2ndSetQuestion.get(i)).append("\n");
                        }

                        new AlertDialog.Builder(Student_Test5Activity.this)
                                .setTitle("Detailed Results")
                                .setMessage(message.toString())
                                .setPositiveButton("OK", (dialog, id) -> {
                                    storeResultsInFirebase(finalEvaluation1, finalEvaluation2);
                                    // Pass data to Graph Activity
                                    Intent graphIntent = new Intent(Student_Test5Activity.this, User_Graph_ResultActivity.class);
                                    graphIntent.putStringArrayListExtra("set1", textInputs);
                                    graphIntent.putStringArrayListExtra("set2", textInputs2ndSetQuestion);
                                    startActivity(graphIntent);
                                    dialog.dismiss();
                                })
                                .show();
                    }
                });
            }
        });
    }

    private void fetchGeneralAnxietyTestSetting(final FirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference("generalAnxietyTestSetting")
                .orderByChild("status").equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<Integer, String> valueMap = new HashMap<>();
                            for (int i = 0; i < 4; i++) {
                                String value = snapshot.child(String.valueOf(i)).getValue(String.class);
                                valueMap.put(i, value);
                            }
                            callback.onCallback(valueMap);
                            return;
                        }
                        callback.onCallback(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("Firebase", "Error fetching anxiety settings", error.toException());
                        callback.onCallback(null);
                    }
                });
    }

    private void fetchMindHealthTestSetting(final FirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference("mindHealthTestSetting")
                .orderByChild("status").equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<Integer, String> valueMap = new HashMap<>();
                            for (int i = 0; i < 4; i++) {
                                String value = snapshot.child(String.valueOf(i)).getValue(String.class);
                                valueMap.put(i, value);
                            }
                            callback.onCallback(valueMap);
                            return;
                        }
                        callback.onCallback(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("Firebase", "Error fetching mental health settings", error.toException());
                        callback.onCallback(null);
                    }
                });
    }

    private int getFinalEvaluation(String[] responses) {
        try {
            double[] values = new double[responses.length];
            for (int i = 0; i < responses.length; i++) {
                values[i] = Double.parseDouble(responses[i]);
            }

            int clusters = 4;
            double[][] memberships = initializeMembershipMatrix(values.length, clusters);
            double[] centroids = {0.5, 1.5, 2.5, 3.0};
            performFCM(values, centroids, memberships, clusters);

            int[] clusterAssignments = new int[values.length];
            for (int i = 0; i < values.length; i++) {
                clusterAssignments[i] = getCluster(memberships[i]);
            }
            return getOverallClusterConclusion(clusterAssignments);
        } catch (Exception e) {
            Log.e("Evaluation", "Error in processing", e);
            return -1;
        }
    }

    private void appendInputs() {
        textInputs2ndSetQuestion.add(editTextText11.getText().toString());
        textInputs2ndSetQuestion.add(editTextText12.getText().toString());
        textInputs2ndSetQuestion.add(editTextText13.getText().toString());
    }


    private boolean validateInput(EditText input) {
        String text = input.getText().toString().trim();
        if (text.isEmpty()) {
            input.setError("Required field");
            return false;
        }
        try {
            int val = Integer.parseInt(text);
            if (val < 0 || val > 3) {
                input.setError("Must be 0-3");
                return false;
            }
        } catch (NumberFormatException e) {
            input.setError("Numbers only");
            return false;
        }
        input.setError(null);
        return true;
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
    private void storeResultsInFirebase(int eval1, int eval2) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("records/" + uid);
        String key = ref.push().getKey();

        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", System.currentTimeMillis());
        data.put("finalEvaluation1", eval1);
        data.put("finalEvaluation2", eval2);

        for (int i = 0; i < textInputs.size(); i++) {
            data.put("set1_question" + (i+1), textInputs.get(i));
        }
        for (int i = 0; i < textInputs2ndSetQuestion.size(); i++) {
            data.put("set2_question" + (i+1), textInputs2ndSetQuestion.get(i));
        }

        ref.child(key).setValue(data)
                .addOnSuccessListener(v -> Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show());
    }
}