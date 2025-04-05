package com.example.vintagestore.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for image processing and optimization
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";
    
    // Maximum dimensions for uploaded images
    private static final int MAX_WIDTH = Constants.MAX_IMAGE_WIDTH;
    private static final int MAX_HEIGHT = Constants.MAX_IMAGE_HEIGHT;
    private static final int QUALITY = Constants.JPEG_QUALITY;
    
    private ImageUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Create a temporary image file for camera captures
     */
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name with timestamp to ensure uniqueness
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );
    }
    
    /**
     * Compress and resize an image from Uri
     * 
     * @param context Application context
     * @param imageUri Uri of the image to process
     * @return File object pointing to the optimized image, or null if processing failed
     */
    public static File optimizeImage(Context context, Uri imageUri) {
        try {
            // Load bitmap from Uri
            InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);
            if (imageStream != null) {
                imageStream.close();
            }
            
            if (originalBitmap == null) {
                Log.e(TAG, "Failed to decode bitmap from Uri: " + imageUri);
                return null;
            }
            
            // Check orientation from Exif data
            InputStream exifStream = context.getContentResolver().openInputStream(imageUri);
            int orientation = ExifInterface.ORIENTATION_NORMAL;
            if (exifStream != null) {
                ExifInterface exif = new ExifInterface(exifStream);
                orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 
                        ExifInterface.ORIENTATION_NORMAL);
                exifStream.close();
            }
            
            // Correct rotation if needed
            Bitmap rotatedBitmap = correctOrientation(originalBitmap, orientation);
            
            // Resize if needed
            Bitmap resizedBitmap = resizeBitmap(rotatedBitmap);
            
            // Save to temporary file
            File outputFile = createImageFile(context);
            FileOutputStream fos = new FileOutputStream(outputFile);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, fos);
            fos.close();
            
            // Clean up memory
            if (resizedBitmap != rotatedBitmap) {
                rotatedBitmap.recycle();
            }
            if (rotatedBitmap != originalBitmap) {
                originalBitmap.recycle();
            }
            
            return outputFile;
            
        } catch (IOException e) {
            Log.e(TAG, "Error processing image: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Resize bitmap if larger than MAX_WIDTH or MAX_HEIGHT
     */
    private static Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        // If bitmap is already smaller than max dimensions, return as is
        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return bitmap;
        }
        
        // Calculate new dimensions while preserving aspect ratio
        float aspectRatio = (float) width / height;
        int newWidth, newHeight;
        
        if (width > height) {
            newWidth = MAX_WIDTH;
            newHeight = Math.round(MAX_WIDTH / aspectRatio);
        } else {
            newHeight = MAX_HEIGHT;
            newWidth = Math.round(MAX_HEIGHT * aspectRatio);
        }
        
        // Create and return the resized bitmap
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
    
    /**
     * Correct image orientation based on Exif data
     */
    private static Bitmap correctOrientation(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.preScale(-1.0f, 1.0f);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.preScale(1.0f, -1.0f);
                break;
            default:
                // No transformation needed
                return bitmap;
        }
        
        // Apply the transformation
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
