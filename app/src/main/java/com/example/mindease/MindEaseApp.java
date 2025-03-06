package com.example.mindease;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MindEaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
