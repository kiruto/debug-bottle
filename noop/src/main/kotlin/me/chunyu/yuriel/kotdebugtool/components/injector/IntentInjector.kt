package me.chunyu.yuriel.kotdebugtool.components

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

    fun add(vararg any: Any) {
        Log.d(TAG, "add")
    }

    fun get(any: Any): Intent? = null

    fun run(any: Any) {
        Log.d(TAG, "run")
    }

}