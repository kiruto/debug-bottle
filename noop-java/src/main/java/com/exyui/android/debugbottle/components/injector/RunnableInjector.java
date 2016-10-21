package com.exyui.android.debugbottle.components.injector;

import android.util.Log;

/**
 * Created by yuriel on 9/7/16.
 */

@SuppressWarnings("unused")
public class RunnableInjector {
    public final static RunnableInjector INSTANCE = new RunnableInjector();
    private static final String TAG = "FakeRunnableInjector";

    private RunnableInjector() {}

    public void put(Object a, Object b) {
        Log.d(TAG, "put");
    }

    public Runnable get() {
        return null;
    }

    public void run(Object a)  {
        Log.d(TAG, "run");
    }
}
