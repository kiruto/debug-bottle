package com.exyui.android.debugbottle.components.injector

import android.app.Activity
import android.util.Log

/**
 * Created by yuriel on 8/30/16.
 */
abstract class Injector {
    private val TAG = "FakeInjector"
    protected val activity: Activity? = null
    protected fun put(a: Any?, b: Any?) {
        Log.d(TAG, "add")
    }

    abstract fun inject()
}