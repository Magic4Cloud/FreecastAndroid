package com.cloud4magic.freecast.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class PermissionHelper {

    public static final int PERMISSION_STORAGE_REQUEST_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final boolean checkSelfStoragePermission(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // Contacts permissions have not been granted.
            return false;
        }
    }

    public static final boolean shouldShowRequestPermissionRationale(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return true;
        }
        return false;
    }

    public static final void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, PERMISSION_STORAGE_REQUEST_CODE);
    }
}
