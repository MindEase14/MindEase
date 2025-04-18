package com.example.mindease;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Student_Test5Activity extends AppCompatActivity {
    private ArrayList<String> textInputs = new ArrayList<>();
    private ArrayList<String> textInputs2ndSetQuestion = new ArrayList<>();
    private Spinner spinner7, spinner8, spinner9;
    private TextView textView26, textView27, textView28;
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

        initializeViews();
        setupSpinners();
        setupFirebaseQuestions();

        Intent intent = getIntent();
        if (intent != null) {
            textInputs = intent.getStringArrayListExtra("inputs");
            textInputs2ndSetQuestion = intent.getStringArrayListExtra("inputs2");
            if (textInputs == null) textInputs = new ArrayList<>();
            if (textInputs2ndSetQuestion == null) textInputs2ndSetQuestion = new ArrayList<>();
        }

        uid = FirebaseUtils.getCurrentUserUID();
        if (uid == null) Log.d("User", "No user logged in");

        Button submit = findViewById(R.id.button9);
        submit.setOnClickListener(v -> {
            if (validateSelections()) {
                appendInputs();
                if (textInputs.isEmpty()) {
                    Toast.makeText(this, "No data to process!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] textBoxes = textInputs.toArray(new String[0]);
                String[] textBoxes2 = textInputs2ndSetQuestion.toArray(new String[0]);
                finalEvaluation1 = getFinalEvaluation(textBoxes);
                finalEvaluation2 = getFinalEvaluation(textBoxes2);
                fetchAndDisplayResults();
            } else {
                Toast.makeText(this, "Please correct the inputs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews() {
        spinner7 = findViewById(R.id.spinner7);
        spinner8 = findViewById(R.id.spinner8);
        spinner9 = findViewById(R.id.spinner9);
        textView26 = findViewById(R.id.textView26);
        textView27 = findViewById(R.id.textView27);
        textView28 = findViewById(R.id.textView28);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                this,
                R.layout.custom_spinner_item,
                getResources().getStringArray(R.array.spinner_options)
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(Color.BLACK);
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner7.setAdapter(adapter);
        spinner8.setAdapter(adapter);
        spinner9.setAdapter(adapter);
    }

    private void setupFirebaseQuestions() {
        DatabaseReference questionRef = FirebaseDatabase.getInstance().getReference().child("mindHealthQuestionSetting");
        questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataSnapshot activeConfig = findActiveConfig(snapshot);
                if (activeConfig == null) return;
                try {
                    textView26.setText("7. " + activeConfig.child("q7").getValue(String.class));
                    textView27.setText("8. " + activeConfig.child("q8").getValue(String.class));
                    textView28.setText("9. " + activeConfig.child("q9").getValue(String.class));
                } catch (Exception e) { Log.e("Firebase", "Error loading questions"); }
            }
            @Override
            public void onCancelled(DatabaseError error) { Log.e("Firebase", "Database error"); }
        });
    }

    private void appendInputs() {
        textInputs2ndSetQuestion.add(String.valueOf(spinner7.getSelectedItemPosition() - 1));
        textInputs2ndSetQuestion.add(String.valueOf(spinner8.getSelectedItemPosition() - 1));
        textInputs2ndSetQuestion.add(String.valueOf(spinner9.getSelectedItemPosition() - 1));
    }

    private boolean validateSelections() {
        if (spinner7.getSelectedItemPosition() == 0) { showToast("Select option for Q7"); return false; }
        if (spinner8.getSelectedItemPosition() == 0) { showToast("Select option for Q8"); return false; }
        if (spinner9.getSelectedItemPosition() == 0) { showToast("Select option for Q9"); return false; }
        return true;
    }

    private DataSnapshot findActiveConfig(DataSnapshot snapshot) {
        for (DataSnapshot config : snapshot.getChildren()) {
            if (config.hasChild("status") && Boolean.TRUE.equals(config.child("status").getValue(Boolean.class))) {
                return config;
            }
        }
        return null;
    }

    private void fetchAndDisplayResults() {
        final StringBuilder message = new StringBuilder();
        fetchGeneralAnxietyTestSetting(anxietyMap -> {
            fetchMindHealthTestSetting(mindHealthMap -> {
                String eval1Str = anxietyMap != null && finalEvaluation1 >= 1 && finalEvaluation1 <= 4 ? anxietyMap.get(finalEvaluation1 - 1) : "N/A";
                String eval2Str = mindHealthMap != null && finalEvaluation2 >= 1 && finalEvaluation2 <= 4 ? mindHealthMap.get(finalEvaluation2 - 1) : "N/A";
                message.append("Evaluation 1: ").append(eval1Str).append("\nEvaluation 2: ").append(eval2Str).append("\n\n");
                message.append("Set 1 (Anxiety)\n");
                for (int i = 0; i < textInputs.size(); i++) message.append("Q").append(i+1).append(": ").append(textInputs.get(i)).append("\n");
                message.append("\nSet 2 (Mental Health)\n");
                for (int i = 0; i < 8; i++) message.append("Q").append(i+1).append(": ").append(textInputs2ndSetQuestion.get(i)).append("\n");
                new AlertDialog.Builder(Student_Test5Activity.this)
                        .setTitle("Results")
                        .setMessage(message.toString())
                        .setPositiveButton("OK", (dialog, id) -> {
                            storeResultsInFirebase(finalEvaluation1, finalEvaluation2);
                            startActivity(new Intent(Student_Test5Activity.this, User_Graph_ResultActivity.class));
                            dialog.dismiss();
                        }).show();
            });
        });
    }

    private void fetchGeneralAnxietyTestSetting(final FirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference("generalAnxietyTestSetting")
                .orderByChild("status").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<Integer, String> valueMap = new HashMap<>();
                            for (int i = 0; i < 4; i++) valueMap.put(i, snapshot.child(String.valueOf(i)).getValue(String.class));
                            callback.onCallback(valueMap);
                            return;
                        }
                        callback.onCallback(null);
                    }
                    @Override
                    public void onCancelled(DatabaseError error) { callback.onCallback(null); }
                });
    }

    private void fetchMindHealthTestSetting(final FirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference("mindHealthTestSetting")
                .orderByChild("status").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<Integer, String> valueMap = new HashMap<>();
                            for (int i = 0; i < 4; i++) valueMap.put(i, snapshot.child(String.valueOf(i)).getValue(String.class));
                            callback.onCallback(valueMap);
                            return;
                        }
                        callback.onCallback(null);
                    }
                    @Override
                    public void onCancelled(DatabaseError error) { callback.onCallback(null); }
                });
    }

    private int getFinalEvaluation(String[] responses) {
        try {
            double[] values = new double[responses.length];
            for (int i = 0; i < responses.length; i++) values[i] = Double.parseDouble(responses[i]);
            int clusters = 4;
            double[][] memberships = initializeMembershipMatrix(values.length, clusters);
            double[] centroids = {0.5, 1.5, 2.5, 3.0};
            performFCM(values, centroids, memberships, clusters);
            int[] clusterAssignments = new int[values.length];
            for (int i = 0; i < values.length; i++) clusterAssignments[i] = getCluster(memberships[i]);
            return getOverallClusterConclusion(clusterAssignments);
        } catch (Exception e) { return -1; }
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
            for (int j = 0; j < numClusters; j++) memberships[i][j] /= sum;
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

        int maxIterations = 200;
        double epsilon = 0.01;
        for (int iter = 0; iter < maxIterations; iter++) {
            double[] previousCentroids = centroids.clone();
            for (int j = 0; j < numClusters; j++) {
                double numerator = 0, denominator = 0;
                for (int i = 0; i < responses.length; i++) {
                    double membership = Math.pow(memberships[i][j], 2);
                    numerator += membership * responses[i];
                    denominator += membership;
                }
                centroids[j] = denominator == 0 ? centroids[j] : numerator / denominator;
            }
            if (maxDifference(centroids, previousCentroids) < epsilon) break;
            for (int i = 0; i < responses.length; i++) {
                for (int j = 0; j < numClusters; j++) {
                    double sum = 0;
                    for (int k = 0; k < numClusters; k++) {
                        double ratio = (Math.abs(responses[i] - centroids[j]) + epsilon) / (Math.abs(responses[i] - centroids[k]) + epsilon);
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
        HashMap<Integer, Integer> clusterCounts = new HashMap<>();
        for (int cluster : clusters) clusterCounts.put(cluster, clusterCounts.getOrDefault(cluster, 0) + 1);
        int maxCluster = -1, maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : clusterCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCluster = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return maxCluster + 1;
    }

    private boolean allResponsesEqual(double[] responses) {
        double first = responses[0];
        for (double response : responses) if (response != first) return false;
        return true;
    }

    private double maxDifference(double[] a, double[] b) {
        double maxDiff = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = Math.abs(a[i] - b[i]);
            if (diff > maxDiff) maxDiff = diff;
        }
        return maxDiff;
    }

    private void storeResultsInFirebase(int eval1, int eval2) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("records/" + uid);
        String key = ref.push().getKey();
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", System.currentTimeMillis());
        data.put("finalEvaluation1", eval1);
        data.put("finalEvaluation2", eval2);
        for (int i = 0; i < textInputs.size(); i++) data.put("set1_question" + (i+1), textInputs.get(i));
        for (int i = 0; i < textInputs2ndSetQuestion.size(); i++) data.put("set2_question" + (i+1), textInputs2ndSetQuestion.get(i));
        ref.child(key).setValue(data)
                .addOnSuccessListener(v -> Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}