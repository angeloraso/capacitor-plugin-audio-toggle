package com.angeloraso.plugins.audiotoggle.android;

import android.os.Build;

public class BuildWrapper {

    public int getVersion() {
        return Build.VERSION.SDK_INT;
    }
}
