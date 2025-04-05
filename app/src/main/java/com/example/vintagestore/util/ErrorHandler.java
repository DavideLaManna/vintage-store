package com.example.vintagestore.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

/**
 * Utility class for consistent error handling throughout the app
 */
public class ErrorHandler {
    private static final String TAG = "ErrorHandler";
    
    /**
     * Shows a short toast message for non-critical errors
     * 
     * @param context The context
     * @param message The error message to display
     */
    public static void showToast(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Shows a Snackbar with error message and optional retry action
     * 
     * @param view The view to find a parent from
     * @param message The error message to display
     * @param retryActionLabel The label for the retry action button
     * @param retryAction The action to perform on retry
     */
    public static void showSnackbar(View view, String message, String retryActionLabel, 
                                   View.OnClickListener retryAction) {
        if (view != null && message != null) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            
            if (retryAction != null && retryActionLabel != null) {
                snackbar.setAction(retryActionLabel, retryAction);
            }
            
            snackbar.show();
        }
    }
    
    /**
     * Checks if the device has an active internet connection
     * 
     * @param context The context
     * @return true if connected, false otherwise
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        
        ConnectivityManager connectivityManager = (ConnectivityManager) 
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
                
        if (connectivityManager == null) {
            return false;
        }
        
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    /**
     * Handles database operation errors
     * 
     * @param tag The logging tag
     * @param error The exception that occurred
     * @param context Optional context for UI feedback
     */
    public static void handleDatabaseError(String tag, Exception error, Context context) {
        Log.e(tag, "Database error: " + error.getMessage(), error);
        
        if (context != null) {
            showToast(context, "Database operation failed. Please try again.");
        }
        
        // In a production app with crash analytics:
        // FirebaseCrashlytics.getInstance().recordException(error);
    }
    
    /**
     * Handles network operation errors with retry capability
     * 
     * @param activity The activity
     * @param view The view for Snackbar
     * @param error The exception that occurred
     * @param retryAction The action to perform on retry
     */
    public static void handleNetworkError(Activity activity, View view, 
                                         Throwable error, Runnable retryAction) {
        if (activity == null || view == null) {
            return;
        }
        
        Log.e(TAG, "Network error: " + error.getMessage(), error);
        
        // First check if it's a connectivity issue
        if (!isNetworkAvailable(activity)) {
            showSnackbar(view, "No internet connection", "Retry", 
                v -> {
                    if (retryAction != null && isNetworkAvailable(activity)) {
                        retryAction.run();
                    } else {
                        // Still no connection, show message again
                        handleNetworkError(activity, view, error, retryAction);
                    }
                });
            return;
        }
        
        // It's another type of network error
        showSnackbar(view, "Connection problem. Please try again.", "Retry", 
            v -> {
                if (retryAction != null) {
                    retryAction.run();
                }
            });
            
        // In a production app with crash analytics:
        // FirebaseCrashlytics.getInstance().recordException(error);
    }
}
