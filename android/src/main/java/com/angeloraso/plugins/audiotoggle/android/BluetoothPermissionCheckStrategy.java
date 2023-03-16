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
        // for android 12/S or newer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        } else {
            return context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
        }
    }
}
