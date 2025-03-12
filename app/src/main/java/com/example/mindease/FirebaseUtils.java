package com.example.mindease;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {

    // Function to get the UID of the currently logged-in user
    public static String getCurrentUserUID() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in, return the UID
            return currentUser.getUid();
        } else {
            // No user is signed in
            return null;
        }
    }
}
