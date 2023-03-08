package com.angeloraso.plugins.audiotoggle.bluetooth;

public interface BluetoothHeadsetConnectionListener {
    void onBluetoothHeadsetStateChanged(String headsetName);
    void onBluetoothHeadsetActivationError();
}
