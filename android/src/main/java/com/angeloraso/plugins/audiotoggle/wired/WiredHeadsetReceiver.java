package com.angeloraso.plugins.audiotoggle.wired;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.angeloraso.plugins.audiotoggle.android.Logger;

public class WiredHeadsetReceiver extends BroadcastReceiver {

    private static final String TAG = "WiredHeadsetReceiver";
    private static final int STATE_UNPLUGGED = 0;
    private static final int STATE_PLUGGED = 1;
    private static final String INTENT_STATE = "state";
    private static final String INTENT_NAME = "name";

    private final Context context;
    private final Logger logger;
    private WiredDeviceConnectionListener deviceListener;

    public WiredHeadsetReceiver(Context context, Logger logger) {
        this.context = context;
        this.logger = logger;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(INTENT_STATE, STATE_UNPLUGGED);
        String name = intent.getStringExtra(INTENT_NAME);
        if (state == STATE_PLUGGED) {
            logger.d(TAG, "Wired headset device " + (name != null ? name : "") + " connected");
            if (deviceListener != null) {
                deviceListener.onDeviceConnected();
            }
        } else {
            logger.d(TAG, "Wired headset device " + (name != null ? name : "") + " disconnected");
            if (deviceListener != null) {
                deviceListener.onDeviceDisconnected();
            }
        }
    }

    public void start(WiredDeviceConnectionListener deviceListener) {
        this.deviceListener = deviceListener;
        context.registerReceiver(this, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }

    public void stop() {
        deviceListener = null;
        context.unregisterReceiver(this);
    }
}
