package com.angeloraso.plugins.audiotoggle.android;

import android.os.SystemClock;

public class SystemClockWrapper {

    public long elapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }
}
