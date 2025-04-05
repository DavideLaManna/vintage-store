package com.example.vintagestore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vintagestore.ui.auth.LoginActivity;
import com.example.vintagestore.util.Constants;

/**
 * Splash screen that displays on app launch
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_TIME = 1500; // milliseconds
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Make the splash screen full screen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        // Use a handler to delay loading the next activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check if user is already logged in
            SharedPreferences prefs = getSharedPreferences(
                Constants.PREF_FILE_NAME, MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean(Constants.PREF_IS_LOGGED_IN, false);
            
            // Navigate to the appropriate screen
            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            
            startActivity(intent);
            finish();
        }, SPLASH_DISPLAY_TIME);
    }
}
