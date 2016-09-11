package com.exyui.android.debugbottle.core

import android.os.Handler

/**
 * Created by yuriel on 8/8/16.
 */
object __HandlerThread {
    /**
     * Get handler of looper thread
     */
    val timerThreadHandler: Handler = getThread("loop")

    /**
     * Get handler of log-writer thread
     */
    val writeLogFileThreadHandler: Handler = getThread("writelog")

    private fun getThread(name: String): Handler {
        val handlerThread = android.os.HandlerThread("BlockCanaryThread_" + name)
        handlerThread.start()
        return Handler(handlerThread.looper)
    }
}