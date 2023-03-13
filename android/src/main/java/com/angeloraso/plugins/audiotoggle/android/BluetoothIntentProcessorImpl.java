package com.angeloraso.plugins.audiotoggle.android;

import static android.bluetooth.BluetoothDevice.EXTRA_DEVICE;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

public class BluetoothIntentProcessorImpl implements BluetoothIntentProcessor {

    @Override
    public BluetoothDeviceWrapper getBluetoothDevice(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
        if (device != null) {
            return new BluetoothDeviceWrapperImpl(device);
        } else {
            return null;
        }
    }
}
