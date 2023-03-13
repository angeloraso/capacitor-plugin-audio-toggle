package com.angeloraso.plugins.audiotoggle.android;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import org.jetbrains.annotations.NotNull;

public class BluetoothDeviceWrapperImpl implements BluetoothDeviceWrapper {

    private static final String DEFAULT_DEVICE_NAME = "Bluetooth";

    private final BluetoothDevice device;
    private String name;
    private Integer deviceClass;

    @SuppressLint("MissingPermission")
    BluetoothDeviceWrapperImpl(@NotNull BluetoothDevice device) {
        this.device = device;

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
