package com.exyui.android.debugbottle.components.Injector;

import android.content.Intent;
import android.util.Log;

/**
 * Created by yuriel on 9/7/16.
 */

public class IntentInjector {

    public final static IntentInjector INSTANCE = new IntentInjector();
    private static final String TAG = "FakeIntentInjector";
    private IntentInjector() {}

    public void setActivity(Object a) {
        Log.d(TAG, "setActivity");
    }

    public void put(Object a, Object b) {
        Log.d(TAG, "put");
    }

    public Intent get() {
        return null;
    }

    public void run(Object a)  {
        Log.d(TAG, "run");
    }
}
