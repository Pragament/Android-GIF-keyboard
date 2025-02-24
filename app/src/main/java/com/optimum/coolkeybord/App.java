package com.optimum.coolkeybord;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

public class App extends Application {
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public void onCreate() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        super.onCreate();
    }
}
