package com.example.vintagestore.util;

/**
 * Constants used throughout the application
 */
public class Constants {
    // API related
    public static final int API_CONNECT_TIMEOUT = 30; // seconds
    public static final int API_READ_TIMEOUT = 30; // seconds
    public static final int API_WRITE_TIMEOUT = 30; // seconds
    
    // Database related
    public static final String DATABASE_NAME = "vintage_store_db";
    public static final int DATABASE_VERSION = 1;
    
    // Shared Preferences
    public static final String PREF_FILE_NAME = "vintage_store_prefs";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_USER_EMAIL = "user_email";
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";
    
    // Pagination
    public static final int PAGE_SIZE = 20;
    
    // Request codes
    public static final int REQUEST_IMAGE_CAPTURE = 1001;
    public static final int REQUEST_PICK_IMAGE = 1002;
    public static final int REQUEST_STORAGE_PERMISSION = 2001;
    public static final int REQUEST_CAMERA_PERMISSION = 2002;
    
    // Image handling
    public static final int MAX_IMAGE_WIDTH = 1024;
    public static final int MAX_IMAGE_HEIGHT = 1024;
    public static final int JPEG_QUALITY = 85;
    
    // Error messages
    public static final String ERROR_NETWORK = "Network connection unavailable";
    public static final String ERROR_SERVER = "Server error occurred";
    public static final String ERROR_TIMEOUT = "Connection timed out";
    public static final String ERROR_UNKNOWN = "An unknown error occurred";
    public static final String ERROR_LOGIN_FAILED = "Login failed. Please check your credentials";
    public static final String ERROR_REGISTER_FAILED = "Registration failed. Please try again";
    
    // Success messages
    public static final String SUCCESS_ITEM_ADDED = "Item added successfully";
    public static final String SUCCESS_ITEM_UPDATED = "Item updated successfully";
    public static final String SUCCESS_ITEM_DELETED = "Item deleted successfully";
    public static final String SUCCESS_PURCHASE = "Purchase completed successfully";
    
    // Keys for Intent extras
    public static final String EXTRA_ITEM_ID = "item_id";
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_CHAT_ID = "chat_id";
    
    // Notification channels
    public static final String CHANNEL_MESSAGES = "messages_channel";
    public static final String CHANNEL_TRANSACTIONS = "transactions_channel";
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
}
