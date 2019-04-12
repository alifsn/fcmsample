package com.reza.fcmsample;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class FCMApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
