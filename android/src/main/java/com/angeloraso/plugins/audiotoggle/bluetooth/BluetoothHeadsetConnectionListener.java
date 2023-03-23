package com.angeloraso.plugins.audiotoggle.bluetooth;

public interface BluetoothHeadsetConnectionListener {
    void onBluetoothHeadsetStateChanged(String headsetName);
    void onBluetoothConnected(boolean connected);
    void onBluetoothHeadsetActivationError();
}
