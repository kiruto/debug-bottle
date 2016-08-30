package me.chunyu.yuriel.kotdebugtool.ui

import android.util.Log

/**
 * Created by yuriel on 8/30/16.
 */
object BlockCanary {
    private val TAG = "FakeBlockCanary"
    fun install(any: Any): BlockCanary {
        Log.d(TAG, "install")
        return this
    }

    fun start() {
        Log.d(TAG, "start")
    }
}