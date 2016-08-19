package me.chunyu.yuriel.kotdebugtool.core

import android.os.SystemClock
import android.util.Log
import android.util.Printer

/**
 * Created by yuriel on 8/8/16.
 */
internal class LooperPrinter(
        private var mBlockThresholdMillis: Long = DEFAULT_BLOCK_THRESHOLD_MILLIS,
        private var mBlockListener: (realTimeStart: Long, realTimeEnd: Long,
                                     threadTimeStart: Long, threadTimeEnd: Long) -> Unit) : Printer {

    private var mStartTimeMillis: Long = 0
    private var mStartThreadTimeMillis: Long = 0

    private var mStartedPrinting = false

    override fun println(x: String) {
        //Log.d("printer", x)
        if (!mStartedPrinting) {
            mStartTimeMillis = System.currentTimeMillis()
            mStartThreadTimeMillis = SystemClock.currentThreadTimeMillis()
            mStartedPrinting = true
            startDump()
        } else {
            val endTime = System.currentTimeMillis()
            mStartedPrinting = false
            if (isBlock(endTime)) {
                notifyBlockEvent(endTime)
            }
            stopDump()
        }
    }

    private fun isBlock(endTime: Long): Boolean {
        return endTime - mStartTimeMillis > mBlockThresholdMillis
    }

    private fun notifyBlockEvent(endTime: Long) {
        Log.d("BlockCanary", "notifyBlockEvent: $endTime - $mStartTimeMillis>$mBlockThresholdMillis")
        val startTime = mStartTimeMillis
        val startThreadTime = mStartThreadTimeMillis
        val endThreadTime = SystemClock.currentThreadTimeMillis()
        HandlerThread.writeLogFileThreadHandler.post {
            mBlockListener(startTime, endTime, startThreadTime, endThreadTime)
        }
    }

    private fun startDump() {
        if (null != CanaryCoreMgr.get().threadStackSampler) {
            CanaryCoreMgr.get().threadStackSampler?.start()
        }

//        if (null != CanaryCoreMgr.get()?.cpuSampler) {
            CanaryCoreMgr.get().cpuSampler.start()
//        }
    }

    private fun stopDump() {
        if (null != CanaryCoreMgr.get().threadStackSampler) {
            CanaryCoreMgr.get().threadStackSampler?.stop()
        }

//        if (null != CanaryCoreMgr.get()?.cpuSampler) {
            CanaryCoreMgr.get().cpuSampler.stop()
//        }
    }

    companion object {
        val DEFAULT_BLOCK_THRESHOLD_MILLIS: Long = 3000
    }
}