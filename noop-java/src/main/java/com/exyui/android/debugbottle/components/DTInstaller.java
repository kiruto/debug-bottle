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

    public DTInstaller install(Object a) {
        Log.d(TAG, "install");
        return this;
    }

    public DTInstaller setBlockCanary(Object a) {
        Log.d(TAG, "setBlockCanary");
        return this;
    }

    public DTInstaller setInjector(Object a) {
        Log.d(TAG, "setInjector");
        return this;
    }

    public DTInstaller setOkHttpClient(Object a) {
        Log.d(TAG, "setOkHttpClient");
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
