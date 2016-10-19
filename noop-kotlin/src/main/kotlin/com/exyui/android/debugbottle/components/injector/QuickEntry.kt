@file:Suppress("unused", "UNUSED_PARAMETER")

package com.exyui.android.debugbottle.components.injector

import android.app.Activity
import android.content.Context
import android.util.Log
import java.util.*

/**
 * Created by yuriel on 10/17/16.
 */
object QuickEntry {
    private val TAG = "QuickEntry"

    fun put(a: Any?, b: Any?, c: Any? = null) {
        Log.d(TAG, "put")
    }

    fun get(): OnActivityDisplayedListener? {
        return null
    }

    fun run(a: Any?) {
        Log.d(TAG, "run")
    }

    fun getList(): LinkedHashMap<String, OnActivityDisplayedListener>? {
        return null
    }

    internal fun isEmpty() = true

    interface OnActivityDisplayedListener {
        fun shouldShowEntry(activity: Activity?): Boolean
        fun run(context: Context?)
        fun description(): String?
    }
}