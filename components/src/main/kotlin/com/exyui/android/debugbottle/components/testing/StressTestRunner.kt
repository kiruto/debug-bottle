package com.exyui.android.debugbottle.components.testing

import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import com.exyui.android.debugbottle.components.DTSettings
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Created by yuriel on 9/18/16.
 */
internal object StressTestRunner {
    private val TAG = "StressTestRunner"
    private val processId = android.os.Process.myPid()

    /**
     * Seed value for pseudo-random number generator.
     * If you re-run the stress test with the same seed value, it will generate the same sequence of events.
     */
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

    var eventsCount = 5
        set(value) {
            field = value
            Log.d(TAG, "events count set: $field")
        }

    var sendKeyEvent = false
        set(value) {
            field = value
            Log.d(TAG, "send key event: $field")
        }

    fun start() {
        try {
//            var v = ""
//            (1..depth).filter { it < 4 }.forEach { v += "-v " }
//            val command = arrayOf("monkey", v, "$eventsCount", "--ignore-timeouts")
//            return Runtime.getRuntime().exec(command)
//            fortest()
        } catch (ex: IOException) {
            Log.e(TAG, "getLog failed", ex)
        }
    }

    fun save() {
        DTSettings.testingSeed = seed
        DTSettings.testingSendKeyEvent = sendKeyEvent
        DTSettings.testingLogDepth = depth
        DTSettings.testingEventCount = eventsCount
    }

    fun load() {
        seed = DTSettings.testingSeed
        sendKeyEvent = DTSettings.testingSendKeyEvent
        depth = DTSettings.testingLogDepth
        eventsCount = DTSettings.testingEventCount
    }

    @Deprecated("")
    @Throws(Exception::class)
    private fun fortest() {
//        val binder = ServiceManager?.getService( "window" )
//        val WindowManager = IWindowManager.Stub.asInterface(binder)
        getWindowManager()?.injectPointerEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN, 10f, 10f, 0), true)
    }

    @Deprecated("")
    @Throws(ClassNotFoundException::class,
            NullPointerException::class,
            IllegalAccessException::class,
            IllegalArgumentException::class,
            InvocationTargetException::class)
    private fun getWindowManager(): Any? {
        val getService = Class.forName("android.os.ServiceManager").getMethod("getService", String::class.java)
        val binder = getService.invoke(null, "window") as IBinder? ?: return null
        val asInterface = Class.forName("android.view.IWindowManager\$Stub").getMethod("asInterface", IBinder::class.java)
        return asInterface.invoke(null, binder)
    }

    @Deprecated("")
    private fun Any.injectPointerEvent(event: MotionEvent, bool: Boolean) {
        val method = this@injectPointerEvent.javaClass.getMethod("injectPointerEvent", MotionEvent::class.java, Boolean::class.java)
        method.invoke(this, event, bool)
    }
}