package com.example.vintagestore.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Utility class for network operations and connectivity monitoring
 */
public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    
    private static NetworkUtils instance;
    private final ConnectivityManager connectivityManager;
    private final MutableLiveData<Boolean> isNetworkAvailable = new MutableLiveData<>();
    
    /**
     * Private constructor for singleton pattern
     */
    private NetworkUtils(Context context) {
        connectivityManager = (ConnectivityManager) 
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        // Initialize with current network state
        isNetworkAvailable.setValue(checkNetworkAvailability());
        
        // Register network callback to monitor changes
        registerNetworkCallback();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized NetworkUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkUtils(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Check if a network connection is currently available
     */
    public boolean checkNetworkAvailability() {
        if (connectivityManager == null) {
            return false;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            
            NetworkCapabilities capabilities = 
                    connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
    
    /**
     * Register a network callback to monitor connectivity changes
     */
    private void registerNetworkCallback() {
        if (connectivityManager == null) {
            return;
        }
        
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
                
        connectivityManager.registerNetworkCallback(networkRequest, 
                new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                isNetworkAvailable.postValue(true);
            }
            
            @Override
            public void onLost(@NonNull Network network) {
                isNetworkAvailable.postValue(false);
            }
            
            @Override
            public void onUnavailable() {
                isNetworkAvailable.postValue(false);
            }
        });
    }
    
    /**
     * Get LiveData to observe network state changes
     */
    public LiveData<Boolean> getNetworkAvailability() {
        return isNetworkAvailable;
    }
}
