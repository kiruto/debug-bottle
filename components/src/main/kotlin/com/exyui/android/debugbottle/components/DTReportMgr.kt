package com.exyui.android.debugbottle.components

import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuriel on 9/13/16.
 */
internal abstract class DTReportMgr {
    open val TYPE: String = ".txt"
    abstract val TAG: String

    private val SAVE_DELETE_LOCK = Any()

    open val FILE_NAME_FORMATTER = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.getDefault())
    open val TIME_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    open val OBSOLETE_DURATION = 2 * 24 * 3600 * 1000L

    abstract val logPath: String
    abstract val filePrefix: String

    val path: String
        get() {
            val state = Environment.getExternalStorageState()

            if (Environment.MEDIA_MOUNTED == state && Environment.getExternalStorageDirectory().canWrite()) {
                return Environment.getExternalStorageDirectory().path + logPath
            }
            return Environment.getDataDirectory().absolutePath + logPath
        }

    internal val logFiles: Array<File>?
        get() {
            val f = detectedFileDirectory()
            if (f.exists() && f.isDirectory) {
                return f.listFiles() { dir, filename ->
                    filename.endsWith(TYPE)
                }
            }
            return null
        }

    internal fun detectedFileDirectory(): File {
        val directory = File(path)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    fun deleteLogFiles() {
        synchronized(SAVE_DELETE_LOCK) {
            try {
                val f = logFiles
                for (aF in f?: return) {
                    aF.delete()
                }
            } catch (e: Throwable) {
                Log.e(TAG, "deleteLogFiles: ", e)
            }
        }
    }

    fun cleanOldFiles() {
        val handlerThread = HandlerThread("DT_file_cleaner")
        handlerThread.start()
        Handler(handlerThread.looper).post {
            val now = System.currentTimeMillis()
            val f = logFiles
            synchronized(SAVE_DELETE_LOCK) {
                for (aF in f?: return@synchronized) {
                    if (now - aF.lastModified() > OBSOLETE_DURATION) {
                        aF.delete()
                    }
                }
            }
        }
    }

    internal fun saveLogToSDCard(logFileNamePrefix: String, str: String): String {
        var path = ""
        var writer: BufferedWriter? = null
        try {
            val file = detectedFileDirectory()
            val time = System.currentTimeMillis()
            path = "${file.absolutePath}/$logFileNamePrefix-${FILE_NAME_FORMATTER.format(time)}$TYPE"
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

    /**
     * Save log to file

     * @param str block log string
     * *
     * @return log file path
     */
    fun saveLog(str: String): String {
        var path: String = ""
        synchronized(SAVE_DELETE_LOCK) {
            path = saveLogToSDCard(filePrefix, str)
        }
        return path
    }
}