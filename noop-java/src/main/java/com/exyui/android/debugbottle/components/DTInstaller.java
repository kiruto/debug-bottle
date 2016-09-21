package com.exyui.android.debugbottle.components;

import android.util.Log;

/**
 * Created by yuriel on 9/7/16.
 */

@SuppressWarnings("unused")
public class DTInstaller {
    private static final String TAG = "FakeInstaller";
    public static final DTInstaller INSTANCE = new DTInstaller();

    private DTInstaller() {}

    public DTInstaller install(Object app) {
        Log.d(TAG, "install");
        return this;
    }

    public DTInstaller setBlockCanary(Object context) {
        Log.d(TAG, "setBlockCanary");
        return this;
    }

    public DTInstaller setInjector(Object injector) {
        Log.d(TAG, "setInjector");
        return this;
    }

    public DTInstaller setOkHttpClient(Object client) {
        Log.d(TAG, "setOkHttpClient");
        return this;
    }

    public DTInstaller setHttpLogPath(Object path) {
        Log.d(TAG, "setHttpLogPath");
        return this;
    }

    public DTInstaller setCrashLogPath(Object path) {
        Log.d(TAG, "setCrashLogPath");
        return this;
    }

    public DTInstaller setNotificationIcon(Object id) {
        Log.d(TAG, "setNotificationIcon");
        return this;
    }

    public DTInstaller setNotificationTitle(Object title) {
        Log.d(TAG, "setNotificationTitle");
        return this;
    }

    public DTInstaller setNotificationMessage(Object message) {
        Log.d(TAG, "setNotificationMessage");
        return this;
    }

    public DTInstaller enable() {
        Log.d(TAG, "enable");
        return this;
    }

    public DTInstaller disable() {
        Log.d(TAG, "disable");
        return this;
    }

    public void run() {
        Log.d(TAG, "run");
    }

    public void kill() {
        Log.d(TAG, "kill");
    }
}
