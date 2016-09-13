package com.exyui.android.debugbottle.components.crash

/**
 * Created by yuriel on 9/13/16.
 */
internal object DTCrashHandler: Thread.UncaughtExceptionHandler {
    private var defaultExceptionHandler: Thread.UncaughtExceptionHandler? = null

    override fun uncaughtException(thread: Thread?, ex: Throwable?) {
        if (null != thread && null != ex) {
            val log = CrashBlock.newInstance(thread, ex)
            CrashReportFileMgr.saveLog(log.toString())
        }
        defaultExceptionHandler?.uncaughtException(thread, ex)
    }

    fun install() {
        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }
}