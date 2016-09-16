package com.exyui.android.debugbottle.components

import android.util.Log

/**
 * Created by yuriel on 8/30/16.
 */
object DTInstaller {
    private val TAG = "FakeInstaller"

    fun install(any: Any?): DTInstaller {
        Log.d(TAG, "install")
        return this
    }

    fun setBlockCanary(any: Any?): DTInstaller {
        Log.d(TAG, "setBlockCanary")
        return this
    }

    fun setInjector(any: Any?): DTInstaller {
        Log.d(TAG, "setInjector")
        return this
    }

    fun setOkHttpClient(any: Any): DTInstaller {
        Log.d(TAG, "setOkHttpClient")
        return this
    }

    fun setHttpLogPath(any: Any): DTInstaller {
        Log.d(TAG, "setHttpLogPath")
        return this
    }

    fun setCrashLogPath(any: Any): DTInstaller {
        Log.d(TAG, "setCrashLogPath")
        return this
    }

    fun enable(): DTInstaller {
        Log.d(TAG, "enable")
        return this
    }

    fun disable(): DTInstaller {
        Log.d(TAG, "disable")
        return this
    }

    fun run() {
        Log.d(TAG, "run")
    }

    fun kill() {
        Log.d(TAG, "kill")
    }
}