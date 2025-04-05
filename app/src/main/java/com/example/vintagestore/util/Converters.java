package com.example.vintagestore.util;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Type converters for Room database.
 * Handles conversion between complex types and database-storable types.
 */
public class Converters {
    
    private static final Gson gson = new Gson();
    
    /**
     * Converts a list of strings to a JSON string for storage in the database.
     * @param list The list of strings to convert
     * @return JSON string representation of the list
     */
    @TypeConverter
    public static String fromStringList(List<String> list) {
        if (list == null) {
            return null;
        }
        return gson.toJson(list);
    }
    
    /**
     * Converts a JSON string to a list of strings.
     * @param value The JSON string to convert
     * @return List of strings parsed from the JSON
     */
    @TypeConverter
    public static List<String> toStringList(String value) {
        if (value == null) {
            return null;
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, listType);
    }
    
    /**
     * Converts a timestamp to a string representation.
     * @param timestamp The timestamp to convert
     * @return String representation of the timestamp
     */
    @TypeConverter
    public static String fromTimestamp(Long timestamp) {
        return timestamp == null ? null : timestamp.toString();
    }
    
    /**
     * Converts a string representation to a timestamp.
     * @param value The string to convert
     * @return Timestamp parsed from the string
     */
    @TypeConverter
    public static Long toTimestamp(String value) {
        return value == null ? null : Long.parseLong(value);
    }
}