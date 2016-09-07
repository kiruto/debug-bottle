package com.exyui.android.debugbottle.ui;

import android.util.Log;

/**
 * Created by yuriel on 9/7/16.
 */

public class BlockCanary {
    public static final BlockCanary INSTANCE = new BlockCanary();
    private BlockCanary() {}
    private static final String TAG = "FakeBlockCanary";
    public BlockCanary install(Object a) {
        Log.d(TAG, "install");
        return this;
    }

    public void start() {
        Log.d(TAG, "start");
    }
}
