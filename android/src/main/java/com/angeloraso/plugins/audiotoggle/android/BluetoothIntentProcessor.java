package com.angeloraso.plugins.audiotoggle.android;

import android.content.Context;
import android.content.Intent;

public interface BluetoothIntentProcessor {
    BluetoothDeviceWrapper getBluetoothDevice(Context context, Intent intent);
}
