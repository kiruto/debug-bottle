package com.exyui.android.debugbottle.components.injector

import android.content.Intent
import android.util.Log

/**
 * Created by yuriel on 8/30/16.
 */
object IntentInjector {
    private val TAG = "FakeIntentInjector"

    fun setActivity(any: Any) {
        Log.d(TAG, "setActivity")
    }

    fun put(a: Any?, b: Any?) {
        Log.d(TAG, "add")
    }

    fun get(any: Any): Intent? = null

    fun run(any: Any) {
        Log.d(TAG, "run")
    }

}