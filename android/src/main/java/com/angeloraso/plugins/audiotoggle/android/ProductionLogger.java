package com.angeloraso.plugins.audiotoggle.android;

import android.util.Log;

public class ProductionLogger implements Logger {

    private static final String TAG_PREFIX = "AS/";
    private boolean loggingEnabled;

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public ProductionLogger(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    @Override
    public void d(String tag, String message) {
        if (loggingEnabled) {
            Log.d(createTag(tag), message);
        }
    }

    @Override
    public void w(String tag, String message) {
        if (loggingEnabled) {
            Log.w(createTag(tag), message);
        }
    }

    @Override
    public void e(String tag, String message) {
        if (loggingEnabled) {
            Log.e(createTag(tag), message);
        }
    }

    @Override
    public void e(String tag, String message, Throwable throwable) {
        if (loggingEnabled) {
            Log.e(createTag(tag), message, throwable);
        }
    }

    private String createTag(String tag) {
        return TAG_PREFIX + tag;
    }
}
