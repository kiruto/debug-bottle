package com.exyui.android.debugbottle.components.injector

import android.util.Log

/**
 * Created by yuriel on 8/30/16.
 */
object RunnableInjector {
    private val TAG = "FakeRunnableInjector"

    fun put(a: Any?, b: Any?) {
        Log.d(TAG, "add")
    }

    fun get(any: Any): Runnable? = null

    fun run(any: Any) {
        Log.d(TAG, "run")
    }

}