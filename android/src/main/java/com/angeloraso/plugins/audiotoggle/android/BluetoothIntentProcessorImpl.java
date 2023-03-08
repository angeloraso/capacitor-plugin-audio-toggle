package com.angeloraso.plugins.audiotoggle.android;

import static android.bluetooth.BluetoothDevice.EXTRA_DEVICE;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

public class BluetoothIntentProcessorImpl implements BluetoothIntentProcessor {

    @Override
    public BluetoothDeviceWrapper getBluetoothDevice(Context context, Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
        if (device != null) {
            return new BluetoothDeviceWrapperImpl(context, device);
        } else {
            return null;
        }
    }
}
