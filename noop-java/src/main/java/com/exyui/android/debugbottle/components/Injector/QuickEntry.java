package com.exyui.android.debugbottle.components.Injector;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.LinkedHashMap;

/**
 * Created by yuriel on 10/17/16.
 */

@SuppressWarnings("unused")
public class QuickEntry {
    private final String TAG = "QuickEntry";
    public void put(Object a, Object b, Object c) {
        Log.d(TAG, "put");
    }
    public void put(Object a, Object b) {
        Log.d(TAG, "put");
    }
    public OnActivityDisplayedListener get() {
        return null;
    }
    public void run(Object a) {
        Log.d(TAG, "run");
    }
    public LinkedHashMap<String, OnActivityDisplayedListener> getList() {
        return null;
    }
    interface OnActivityDisplayedListener {
        boolean shouldShowEntry(Activity activity);
        void run(Context context);
    }
}
