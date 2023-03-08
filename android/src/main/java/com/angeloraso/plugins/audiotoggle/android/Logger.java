package com.angeloraso.plugins.audiotoggle.android;

public interface Logger {
    boolean isLoggingEnabled();
    void setLoggingEnabled(boolean loggingEnabled);
    void d(String tag, String message);
    void w(String tag, String message);
    void e(String tag, String message);
    void e(String tag, String message, Throwable throwable);
}
