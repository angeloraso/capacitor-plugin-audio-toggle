package com.angeloraso.plugins.audiotoggle.bluetooth;

import android.os.Handler;
import com.angeloraso.plugins.audiotoggle.android.Logger;
import com.angeloraso.plugins.audiotoggle.android.SystemClockWrapper;
import java.util.concurrent.TimeoutException;

public class BluetoothScoJob {

    public static final long TIMEOUT = 5000L;
    private static final String TAG = "BluetoothScoJob";

    private final Logger logger;
    private final Handler bluetoothScoHandler;
    private final SystemClockWrapper systemClockWrapper;

    BluetoothScoRunnable bluetoothScoRunnable = null;

    public BluetoothScoJob(Logger logger, Handler bluetoothScoHandler, SystemClockWrapper systemClockWrapper) {
        this.logger = logger;
        this.bluetoothScoHandler = bluetoothScoHandler;
        this.systemClockWrapper = systemClockWrapper;
    }

    protected void scoAction() {}

    protected void scoTimeOutAction() {}

    public void executeBluetoothScoJob() {
        // cancel existing runnable
        if (bluetoothScoRunnable != null) {
            bluetoothScoHandler.removeCallbacks(bluetoothScoRunnable);
        }

        BluetoothScoRunnable runnable = new BluetoothScoRunnable();
        bluetoothScoRunnable = runnable;
        bluetoothScoHandler.post(runnable);
        logger.d(TAG, "Scheduled bluetooth sco job");
    }

    public void cancelBluetoothScoJob() {
        if (bluetoothScoRunnable != null) {
            bluetoothScoHandler.removeCallbacks(bluetoothScoRunnable);
            bluetoothScoRunnable = null;
            logger.d(TAG, "Canceled bluetooth sco job");
        }
    }

    private class BluetoothScoRunnable implements Runnable {

        private final long startTime;
        private long elapsedTime;

        BluetoothScoRunnable() {
            startTime = systemClockWrapper.elapsedRealtime();
            elapsedTime = 0L;
        }

        @Override
        public void run() {
            if (elapsedTime < TIMEOUT) {
                scoAction();
                elapsedTime = systemClockWrapper.elapsedRealtime() - startTime;
                bluetoothScoHandler.postDelayed(this, 500);
            } else {
                logger.e(TAG, "Bluetooth sco job timed out", new TimeoutException());
                scoTimeOutAction();
                cancelBluetoothScoJob();
            }
        }
    }
}
