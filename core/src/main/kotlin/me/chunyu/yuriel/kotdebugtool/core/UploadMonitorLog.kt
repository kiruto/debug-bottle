package me.chunyu.yuriel.kotdebugtool.core

import android.util.Log
import me.chunyu.yuriel.kotdebugtool.core.log.BlockCanaryInternals
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuriel on 8/8/16.
 */
object UploadMonitorLog {
    private val TAG = "UploadMonitorLog"
    private val FORMAT = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())

    private fun zipFile(): File {
        var timeString = java.lang.Long.toString(System.currentTimeMillis())
        try {
            timeString = FORMAT.format(Date())
        } catch (e: Throwable) {
            Log.e(TAG, "zipFile: ", e)
        }

        val zippedFile = LogWriter.generateTempZipFile("Monitor_looper_" + timeString)
        CanaryCoreMgr.context?.zipLogFile(BlockCanaryInternals.logFiles, zippedFile)
        LogWriter.deleteLogFiles()
        return zippedFile
    }

    fun forceZipLogAndUpload() {
        HandlerThread.writeLogFileThreadHandler.post {
            val file = zipFile()
            if (file.exists()) {
                CanaryCoreMgr.context?.uploadLogFile(file)
            }
        }
    }
}
