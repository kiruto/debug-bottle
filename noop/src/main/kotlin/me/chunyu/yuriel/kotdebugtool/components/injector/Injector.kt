package me.chunyu.yuriel.kotdebugtool.components.injector

import android.util.Log

/**
 * Created by yuriel on 8/30/16.
 */
abstract class Injector {
    private val TAG = "FakeInjector"
    protected fun put(a: Any?, b: Any?) {
        Log.d(TAG, "add")
    }

    abstract fun inject()
}