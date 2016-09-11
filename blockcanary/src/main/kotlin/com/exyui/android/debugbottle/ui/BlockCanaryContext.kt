package com.exyui.android.debugbottle.ui

import android.content.Context
import com.exyui.android.debugbottle.core.BLOCK_THRESHOLD
import com.exyui.android.debugbottle.core.DEFAULT_BLOCK_THRESHOLD
import com.exyui.android.debugbottle.core.DT_SETTING_STORE_FILE
import com.exyui.android.debugbottle.core.__IBlockCanaryContext
import java.io.File

/**
 * Created by yuriel on 8/9/16.
 */
open class BlockCanaryContext(override val context: Context) : __IBlockCanaryContext {

    /**
     * qualifier which can specify this installation, like version + flavor

     * @return apk qualifier
     */
    override val qualifier: String = "Unspecified"

    /**
     * Get user id

     * @return user id
     */
    override val uid: String = "0"

    /**
     * Network type

     * @return String like 2G, 3G, 4G, wifi, etc.
     */
    override val networkType: String = "UNKNOWN"

    /**
     * Config monitor duration, after this time BlockCanary will stop, use
     * with [BlockCanary]'s isMonitorDurationEnd

     * @return monitor last duration (in hour)
     */
    open val configDuration: Int = 99999

    /**
     * Config block threshold (in millis), dispatch over this duration is regarded as a BLOCK. You may set it
     * from performance of device.

     * @return threshold in mills
     */
    override val configBlockThreshold: Long
        get() {
            return context.getSharedPreferences(DT_SETTING_STORE_FILE, Context.MODE_PRIVATE)
                    ?.getLong(BLOCK_THRESHOLD, DEFAULT_BLOCK_THRESHOLD) ?: DEFAULT_BLOCK_THRESHOLD
        }

    /**
     * If need notification and block ui

     * @return true if need, else if not need.
     */
    override val isNeedDisplay: Boolean = true

    /**
     * Path to save log, like "/blockcanary/log"

     * @return path of log files
     */
    override val logPath: String = "/ktdebugtools/performance"

    /**
     * Zip log file

     * @param src  files before compress
     * *
     * @param dest files compressed
     * *
     * @return true if compression is successful
     */
    override fun zipLogFile(src: Array<File>?, dest: File): Boolean = false

    /**
     * Upload log file

     * @param zippedFile zipped file
     */
    override fun uploadLogFile(zippedFile: File) {
        throw UnsupportedOperationException()
    }

    /**
     * Config string prefix to determine how to fold stack

     * @return string prefix, null if use process name.
     */
    override val stackFoldPrefix: String? = null

    /**
     * Thread stack dump interval, use when block happens, BlockCanary will dump on main thread
     * stack according to current sample cycle.
     *
     *
     * PS: Because the implementation mechanism of Looper, real dump interval would be longer than
     * the period specified here (longer if cpu is busier)
     *
     * @return dump interval(in millis)
     */
    override val configDumpIntervalMillis: Long = configBlockThreshold

    companion object {

        private var sInstance: BlockCanaryContext? = null

        fun init(blockCanaryContext: BlockCanaryContext) {
            sInstance = blockCanaryContext
        }

        fun get(): BlockCanaryContext {
            if (sInstance == null) {
                throw RuntimeException("BlockCanaryContext not init")
            } else {
                return sInstance!!
            }
        }
    }
}