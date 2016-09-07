package com.exyui.android.debugbottle.components.Injector;

import android.app.Activity;
import android.util.Log;

/**
 * Created by yuriel on 9/7/16.
 */

abstract class Injector {
    private static final String TAG = "FakeInjector";
    protected final Activity activity = null;
    protected void put(Object a, Object b) {
        Log.d(TAG, "put");
    }

    abstract void inject();
}
