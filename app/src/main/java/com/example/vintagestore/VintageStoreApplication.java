package com.example.vintagestore;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

/**
 * Main application class for the Vintage Store app.
 * Handles initialization of MultiDex and other app-wide configurations.
 */
public class VintageStoreApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize your app-wide components here
        // For example:
        // - Crash reporting
        // - Analytics
        // - SharedPreferences initialization
        // - Logging configuration
        
        // Example for tracking app launch analytics (in a real app)
        // FirebaseAnalytics.getInstance(this);
    }
}
