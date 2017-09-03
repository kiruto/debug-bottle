package com.exyui.android.debugbottle.core

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by yuriel on 8/8/16.
 */
internal abstract class Sampler(sampleIntervalMillis: Long) {
    private val DEFAULT_SAMPLE_INTERVAL_MILLIS = 300

    private var mIsDumping = AtomicBoolean(false)
    internal var mSampleIntervalMillis: Long = 0

    private val mRunnable: Runnable by lazy {
        Runnable {
            doSample()

            // If non-stop, doSample again after mSampleIntervalMillis elapses.
            if (mIsDumping.get()) {
                __HandlerThread.timerThreadHandler.postDelayed(mRunnable,
                        mSampleIntervalMillis)
            }
        }
    }

    init {
        var millis = sampleIntervalMillis
        if (0L == millis) {
            millis = DEFAULT_SAMPLE_INTERVAL_MILLIS.toLong()
        }
        mSampleIntervalMillis = millis
    }

    /**
     * start sampling.
     */
    open fun start() {
        // exit if dumping
        if (mIsDumping.get()) {
            return
        }
        mIsDumping.set(true)

        __HandlerThread.timerThreadHandler.removeCallbacks(mRunnable)
        __HandlerThread.timerThreadHandler.postDelayed(mRunnable,
                __CanaryCoreMgr.get().sampleDelay)
    }

    /**
     * stop sampling
     */
    fun stop() {
        // exit if not dumping
        if (!mIsDumping.get()) {
            return
        }
        mIsDumping.set(false)
        __HandlerThread.timerThreadHandler.removeCallbacks(mRunnable)
    }

    internal abstract fun doSample()
}