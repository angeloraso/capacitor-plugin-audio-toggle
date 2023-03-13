package com.angeloraso.plugins.audiotoggle.android;

import android.content.Intent;

public interface BluetoothIntentProcessor {
    BluetoothDeviceWrapper getBluetoothDevice(Intent intent);
}
