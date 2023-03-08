package com.angeloraso.plugins.audiotoggle.android;

import android.Manifest;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import org.jetbrains.annotations.NotNull;

public class BluetoothDeviceWrapperImpl implements BluetoothDeviceWrapper {

    private static final String DEFAULT_DEVICE_NAME = "Bluetooth";

    private final BluetoothDevice device;
    private String name;
    private Integer deviceClass;

    BluetoothDeviceWrapperImpl(Context context, @NotNull BluetoothDevice device) {
        this.device = device;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        this.name = device.getName() != null ? device.getName() : DEFAULT_DEVICE_NAME;
        BluetoothClass bluetoothClass = device.getBluetoothClass();
        this.deviceClass = bluetoothClass != null ? bluetoothClass.getDeviceClass() : null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getDeviceClass() {
        return deviceClass;
    }
}
