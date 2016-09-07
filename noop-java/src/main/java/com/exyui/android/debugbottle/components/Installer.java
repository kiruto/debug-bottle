package com.exyui.android.debugbottle.components;

import android.util.Log;

/**
 * Created by yuriel on 9/7/16.
 */

public class Installer {
    private static final String TAG = "FakeInstaller";
    public static final Installer INSTANCE = new Installer();

    private Installer() {}

    public Installer install(Object a) {
        Log.d(TAG, "install");
        return this;
    }

    public Installer setBlockCanary(Object a) {
        Log.d(TAG, "setBlockCanary");
        return this;
    }

    public Installer setInjector(Object a) {
        Log.d(TAG, "setInjector");
        return this;
    }

    public Installer setPackageName(Object a) {
        Log.d(TAG, "setPackageName");
        return this;
    }

    public Installer setOkHttpClient(Object a) {
        Log.d(TAG, "setOkHttpClient");
        return this;
    }

    public Installer enable() {
        Log.d(TAG, "enable");
        return this;
    }

    public Installer disable() {
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
