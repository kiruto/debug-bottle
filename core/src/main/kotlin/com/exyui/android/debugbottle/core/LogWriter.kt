package com.exyui.android.debugbottle.core

import android.util.Log
import com.exyui.android.debugbottle.core.log.BlockCanaryInternals
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuriel on 8/8/16.
 */
object LogWriter {

    private val TAG = "LogWriter"

    private val SAVE_DELETE_LOCK = Any()
    private val FILE_NAME_FORMATTER = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.getDefault())
    private val TIME_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val OBSOLETE_DURATION = 2 * 24 * 3600 * 1000L

    /**
     * Save log to file

     * @param str block log string
     * *
     * @return log file path
     */
    fun saveLooperLog(str: String): String {
        var path: String = ""
        synchronized(SAVE_DELETE_LOCK) {
            path = saveLogToSDCard("looper", str)
        }
        return path
    }

    /**
     * Delete obsolete log files，see also `OBSOLETE_DURATION`
     */
    fun cleanOldFiles() {
        HandlerThread.writeLogFileThreadHandler?.post {
            val now = System.currentTimeMillis()
            val f = BlockCanaryInternals.logFiles
            synchronized(SAVE_DELETE_LOCK) {
                for (aF in f?: return@synchronized) {
                    if (now - aF.lastModified() > OBSOLETE_DURATION) {
                        aF.delete()
                    }
                }
            }
        }
    }

    /**
     * Delete all log files.
     */
    fun deleteLogFiles() {
        synchronized(SAVE_DELETE_LOCK) {
            try {
                val f = BlockCanaryInternals.logFiles
                for (aF in f?: return) {
                    aF.delete()
                }
            } catch (e: Throwable) {
                Log.e(TAG, "deleteLogFiles: ", e)
            }

        }
    }

    private fun saveLogToSDCard(logFileName: String, str: String): String {
        var path = ""
        var writer: BufferedWriter? = null
        try {
            val file = BlockCanaryInternals.detectedBlockDirectory()
            val time = System.currentTimeMillis()
            path = file.absolutePath + "/" + logFileName + "-" + FILE_NAME_FORMATTER.format(time) + ".txt"
            val out = OutputStreamWriter(FileOutputStream(path, true), "UTF-8")

            writer = BufferedWriter(out)
            writer.write("\r\n**********************\r\n")
            writer.write(TIME_FORMATTER.format(time) + "(write log time)")
            writer.write("\r\n")
            writer.write("\r\n")
            writer.write(str)
            writer.write("\r\n")
            writer.flush()
            writer.close()
            writer = null
        } catch (t: Throwable) {
            Log.e(TAG, "saveLogToSDCard: ", t)
        } finally {
            try {
                if (writer != null) {
                    writer.close()
                    writer = null
                }
            } catch (e: Exception) {
                Log.e(TAG, "saveLogToSDCard: ", e)
            }

        }
        return path
    }

    fun generateTempZipFile(filename: String): File {
        return File(BlockCanaryInternals.path + "/" + filename + ".log.zip")
    }
}

