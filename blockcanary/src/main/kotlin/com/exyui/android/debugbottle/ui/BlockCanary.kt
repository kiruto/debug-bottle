package com.exyui.android.debugbottle.ui

import android.content.ComponentName
import android.content.Context
import android.os.Looper
import android.preference.PreferenceManager
import com.exyui.android.debugbottle.core.__CanaryCore
import com.exyui.android.debugbottle.core.__UploadMonitorLog
import com.exyui.android.debugbottle.ui.layout.__DisplayBlockActivity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
import android.content.pm.PackageManager.DONT_KILL_APP
import com.exyui.android.debugbottle.core.__CanaryCoreMgr
import com.exyui.android.debugbottle.ui.layout.__Notifier

/**
 * Created by yuriel on 8/9/16.
 */
class BlockCanary private constructor() {
    private val mBlockCanaryCore: __CanaryCore
    private var mLooperLoggingStarted = false

    init {
        __CanaryCoreMgr.context = BlockCanaryContext.get()
        mBlockCanaryCore = __CanaryCoreMgr.get()
        initNotification()
    }

    /**
     * Start main-thread monitoring.
     */
    fun start() {
        if (!mLooperLoggingStarted) {
            mLooperLoggingStarted = true
            Looper.getMainLooper().setMessageLogging(mBlockCanaryCore.getPrinter())
        }
    }

    /**
     * Stop monitoring.
     */
    fun stop() {
        if (mLooperLoggingStarted) {
            mLooperLoggingStarted = false
            Looper.getMainLooper().setMessageLogging(null)
            mBlockCanaryCore.stopSample()
        }
    }

    /**
     * Zip and upload log files.
     */
    fun upload() {
        __UploadMonitorLog.forceZipLogAndUpload()
    }

    /**
     * Record monitor start time to preference, you may use it when after push which tells start
     * BlockCanary.
     */
    fun recordStartTime() {
        PreferenceManager.getDefaultSharedPreferences(BlockCanaryContext.get().context)
                .edit()
                .putLong("BlockCanary_StartTime", System.currentTimeMillis())
                .commit()
    }

    /**
     * Is monitor duration end, compute from recordStartTime end getConfigDuration.

     * @return true if ended
     */
    val isMonitorDurationEnd: Boolean
        get() {
            val startTime = PreferenceManager
                    .getDefaultSharedPreferences(BlockCanaryContext.get().context)
                    .getLong("BlockCanary_StartTime", 0)
            return startTime != 0L && System.currentTimeMillis() - startTime > BlockCanaryContext.get().configDuration.toLong() * 3600 * 1000
        }

    @SuppressWarnings("unchecked")
    private fun initNotification() {
        if (!BlockCanaryContext.get().isNeedDisplay) {
            return
        }
        mBlockCanaryCore.mOnBlockEventInterceptor = __Notifier()
    }

    companion object {

        private val TAG = "BlockCanary"

        private var sInstance: BlockCanary? = null

        /**
         * Install [BlockCanary]

         * @param context application context
         * *
         * @param blockCanaryContext implementation for [BlockCanaryContext]
         * *
         * @return [BlockCanary]
         */
        fun install(blockCanaryContext: BlockCanaryContext): BlockCanary {
            BlockCanaryContext.init(blockCanaryContext)
            setEnabled(blockCanaryContext.context, __DisplayBlockActivity::class.java, BlockCanaryContext.get().isNeedDisplay)
            return get()
        }

        /**
         * Get [BlockCanary] singleton.

         * @return [BlockCanary] instance
         */
        fun get(): BlockCanary {
            if (sInstance == null) {
                synchronized(BlockCanary::class.java) {
                    if (sInstance == null) {
                        sInstance = BlockCanary()
                    }
                }
            }
            return sInstance!!
        }

        // these lines are originally copied from LeakCanary: Copyright (C) 2015 Square, Inc.
        private val fileIoExecutor = newSingleThreadExecutor("File-IO")

        private fun setEnabledBlocking(appContext: Context, componentClass: Class<*>,
                                       enabled: Boolean) {
            val component = ComponentName(appContext, componentClass)
            val packageManager = appContext.packageManager
            val newState = if (enabled) COMPONENT_ENABLED_STATE_ENABLED else COMPONENT_ENABLED_STATE_DISABLED
            // Blocks on IPC.
            packageManager.setComponentEnabledSetting(component, newState, DONT_KILL_APP)
        }
        // end of lines copied from LeakCanary

        private fun executeOnFileIoThread(runnable: Runnable) {
            fileIoExecutor.execute(runnable)
        }

        private fun newSingleThreadExecutor(threadName: String): Executor {
            return Executors.newSingleThreadExecutor(LeakCanarySingleThreadFactory(threadName))
        }

        private fun setEnabled(context: Context, componentClass: Class<*>,
                               enabled: Boolean) {
            val appContext = context.applicationContext
            executeOnFileIoThread(Runnable { setEnabledBlocking(appContext, componentClass, enabled) })
        }
    }
}
