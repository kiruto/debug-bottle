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
        val timeString = try {
            FORMAT.format(Date())
        } catch (e: Throwable) {
            Log.e(TAG, "zipFile: ", e)
            System.currentTimeMillis().toString()
        }

        return __LogWriter.generateTempZipFile("Monitor_looper_" + timeString).apply {
            __CanaryCoreMgr.context?.zipLogFile(__BlockCanaryInternals.logFiles, this)
            __LogWriter.deleteLogFiles()
        }
    }

    fun forceZipLogAndUpload() {
        __HandlerThread.writeLogFileThreadHandler.post {
            zipFile().let {
                if (it.exists()) {
                    __CanaryCoreMgr.context?.uploadLogFile(it)
                }
            }
        }
    }
}
