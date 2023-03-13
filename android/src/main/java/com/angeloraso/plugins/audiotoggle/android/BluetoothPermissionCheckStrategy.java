package com.angeloraso.plugins.audiotoggle.android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class BluetoothPermissionCheckStrategy implements PermissionsCheckStrategy {

    private Context context;

    public BluetoothPermissionCheckStrategy(final Context context) {
        this.context = context;
    }

    @Override
    public boolean hasPermissions() {
        if (context.getApplicationInfo().targetSdkVersion <= Build.VERSION_CODES.R || Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            return context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
        } else {
            // for android 12/S or newer
            return context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }
    }
}
