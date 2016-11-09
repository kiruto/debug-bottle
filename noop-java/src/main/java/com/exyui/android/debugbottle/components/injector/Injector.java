package com.exyui.android.debugbottle.components.injector;

import android.app.Activity;
import android.util.Log;

/**
 * Created by yuriel on 9/7/16.
 */

@SuppressWarnings("unused")
public abstract class Injector {
    private static final String TAG = "FakeInjector";
    protected final Activity activity = null;
    protected final Activity getActivity() {
        Log.d(TAG, "getActivity");
        return null;
    }
    protected final void put(Object a, Object b) {
        Log.d(TAG, "put");
    }
    protected final void quickEntry(Object a, Object b, Object c) {
        Log.d(TAG, "quickEntry");
    }
    protected final void quickEntry(Object a, Object b) {
        Log.d(TAG, "quickEntry");
    }
    protected final void excludeFromMonkey(Object a) {
        Log.d(TAG, "excludeFromMonkey");
    }

    public abstract void inject();

    public void beforeMonkeyTest() {
        Log.d(TAG, "beforeMonkeyTest");
    }
}
