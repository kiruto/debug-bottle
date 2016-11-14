@file:Suppress("UNUSED_PARAMETER", "unused")

package com.exyui.android.debugbottle.components

import android.util.Log

/**
 * Created by yuriel on 8/30/16.
 */
object DTInstaller {
    private val TAG = "FakeInstaller"

    fun install(app: Any?): DTInstaller {
        Log.d(TAG, "install")
        return this
    }

    fun setBlockCanary(context: Any?): DTInstaller {
        Log.d(TAG, "setBlockCanary")
        return this
    }

    fun setInjector(injector: Any?): DTInstaller {
        Log.d(TAG, "setInjector")
        return this
    }

    fun setOkHttpClient(client: Any?): DTInstaller {
        Log.d(TAG, "setOkHttpClient")
        return this
    }

    fun setHttpLogPath(path: Any?): DTInstaller {
        Log.d(TAG, "setHttpLogPath")
        return this
    }

    fun setCrashLogPath(path: Any?): DTInstaller {
        Log.d(TAG, "setCrashLogPath")
        return this
    }

    fun setNotificationIcon(id: Any?): DTInstaller {
        Log.d(TAG, "setNotificationIcon")
        return this
    }

    fun setNotificationTitle(title: Any?): DTInstaller {
        Log.d(TAG, "setNotificationTitle")
        return this
    }

    fun setNotificationMessage(message: Any?): DTInstaller {
        Log.d(TAG, "setNotificationMessage")
        return this
    }

    fun setNotificationDisplay(display: Boolean) {
        Log.d(TAG, "setNotificationDisplay")
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

    fun getOkHttp3Interceptor(): Any? {
        Log.d(TAG, "getOkHttp3Interceptor")
        return null
    }

    fun kill() {
        Log.d(TAG, "kill")
    }
}