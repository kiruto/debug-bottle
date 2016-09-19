package com.exyui.android.debugbottle.components.testing

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by yuriel on 9/18/16.
 */
internal object MonkeyTestRunner {
    private val TAG = "MonkeyTestRunner"
    private val processId = android.os.Process.myPid()
    var depth = 0
        set(value) {
            if (depth < 4 && depth > 0) {
                field = value
                Log.d(TAG, "depth set: $field")
            }
        }
    var eventsCount = 500
        set(value) {
            field = value
            Log.d(TAG, "events count set: $field")
        }

    fun start(): Process? {
        try {
            var v = ""
            (0..depth).forEach { v += "-v " }
            val command = arrayOf("monkey", v, "$eventsCount", "--ignore-timeouts")
            return Runtime.getRuntime().exec(command)

        } catch (ex: IOException) {
            Log.e(TAG, "getLog failed", ex)
            return null
        }
    }
}