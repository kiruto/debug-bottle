package com.exyui.android.debugbottle.components.Injector;

import android.app.Activity;
import android.util.Log;

/**
 * Created by yuriel on 9/7/16.
 */

@SuppressWarnings("unused")
abstract class Injector {
    private static final String TAG = "FakeInjector";
    protected final Activity activity = null;
    protected void put(Object a, Object b) {
        Log.d(TAG, "put");
    }
    protected void quickEntry(Object a, Object b, Object c) {
        Log.d(TAG, "quickEntry");
    }
    protected void quickEntry(Object a, Object b) {
        Log.d(TAG, "quickEntry");
    }

    abstract void inject();
}
