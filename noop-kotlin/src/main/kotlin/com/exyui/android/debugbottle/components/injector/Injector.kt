@file:Suppress("UNUSED_PARAMETER", "unused")

package com.exyui.android.debugbottle.components.injector

import android.app.Activity
import android.util.Log

/**
 * Created by yuriel on 8/30/16.
 */
abstract class Injector {
    private val TAG = "FakeInjector"
    protected val activity: Activity? = null
    protected fun put(a: Any? = null, b: Any? = null) {
        Log.d(TAG, "add")
    }

    protected fun quickEntry(a: Any? = null, b: Any? = null, c: Any? = null) {
        Log.d(TAG, "quickEntry")
    }

    protected fun excludeFromMonkey(a: Any? = null) {
        Log.d(TAG, "excludeFromMonkey")
    }

    abstract fun inject()
}