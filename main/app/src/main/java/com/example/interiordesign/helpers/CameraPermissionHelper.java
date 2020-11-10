package com.example.interiordesign.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.interiordesign.main.MainActivity;

/**
 * Helper to ask user for camera permission.
 */
public class CameraPermissionHelper {
    private final static int CAMERA_REQUEST_CODE = 0; // ID to identify a camera permission request
    private final static String CAMERA_PERMISSION = Manifest.permission.CAMERA; // Permission to use camera

    /**
     *  Check if camera permission has already been granted.
     */
    public static boolean hasPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Check if rational dialog box for camera permission should be shown.
     * Note: This function is usually called when hasPermission returns false (PERMISSION_DENIED).
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION);
    }


    /**
     * Request permission through a system permission dialog box
     * Note: requestPermissions
     */
    public static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[] {CAMERA_PERMISSION},
                CAMERA_REQUEST_CODE);
    }

    /**
     * Launch Application Setting to grant permission.
     */
    public static void launchPermissionSettings(Activity activity) {
        Intent intent = new Intent(); // Used to launch an Activity
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS); // Action is to show the application details
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }
}
