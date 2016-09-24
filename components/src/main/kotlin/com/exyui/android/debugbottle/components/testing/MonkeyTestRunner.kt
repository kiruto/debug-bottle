package com.exyui.android.debugbottle.components.testing

import android.util.Log
import java.io.IOException

/**
 * Created by yuriel on 9/18/16.
 */
internal object MonkeyTestRunner {
    private val TAG = "MonkeyTestRunner"
    private val processId = android.os.Process.myPid()

    var seed = 1024
        set(value) {
            field = value
            Log.d(TAG, "seed set: $field")
        }

    var depth = 0
        set(value) {
            if (depth >= 0) {
                field = value
                Log.d(TAG, "depth set: $field")
            }
        }
    var eventsCount = 50
        set(value) {
            field = value
            Log.d(TAG, "events count set: $field")
        }

    fun start(): Process? {
        try {
            var v = ""
            (1..depth).filter { it < 4 }.forEach { v += "-v " }
            val command = arrayOf("monkey", v, "$eventsCount", "--ignore-timeouts")
            return Runtime.getRuntime().exec(command)

        } catch (ex: IOException) {
            Log.e(TAG, "getLog failed", ex)
            return null
        }
    }
}