package me.chunyu.yuriel.kotdebugtool.components

import android.util.Log

/**
 * Created by yuriel on 8/30/16.
 */
object RunnableInjector {
    private val TAG = "FakeRunnableInjector"

    fun add(vararg any: Any) {
        Log.d(TAG, "add")
    }

    fun get(any: Any): Runnable? = null

    fun run(any: Any) {
        Log.d(TAG, "run")
    }

}