package com.exyui.android.debugbottle.core

import android.util.Log
import com.exyui.android.debugbottle.core.log.__BlockCanaryInternals
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuriel on 8/8/16.
 */
object __UploadMonitorLog {
    private val TAG = "__UploadMonitorLog"
    private val FORMAT = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())

    private fun zipFile(): File {
        var timeString = java.lang.Long.toString(System.currentTimeMillis())
        try {
            timeString = FORMAT.format(Date())
        } catch (e: Throwable) {
            Log.e(TAG, "zipFile: ", e)
        }

        val zippedFile = __LogWriter.generateTempZipFile("Monitor_looper_" + timeString)
        __CanaryCoreMgr.context?.zipLogFile(__BlockCanaryInternals.logFiles, zippedFile)
        __LogWriter.deleteLogFiles()
        return zippedFile
    }

    fun forceZipLogAndUpload() {
        __HandlerThread.writeLogFileThreadHandler.post {
            val file = zipFile()
            if (file.exists()) {
                __CanaryCoreMgr.context?.uploadLogFile(file)
            }
        }
    }
}
