package com.exyui.android.debugbottle.components

import android.util.Log

/**
 * Created by yuriel on 8/30/16.
 */
object Installer {
    private val TAG = "FakeInstaller"

    fun install(any: Any?): Installer {
        Log.d(TAG, "install")
        return this
    }

    fun setBlockCanary(any: Any?): Installer {
        Log.d(TAG, "setBlockCanary")
        return this
    }

    fun setInjector(any: Any?): Installer {
        Log.d(TAG, "setInjector")
        return this
    }

    fun setPackageName(any: Any?): Installer {
        Log.d(TAG, "setPackageName")
        return this
    }

    fun setOkHttpClient(any: Any): Installer {
        Log.d(TAG, "setOkHttpClient")
        return this
    }

    fun run() {
        Log.d(TAG, "run")
    }

    fun kill() {
        Log.d(TAG, "kill")
    }
}