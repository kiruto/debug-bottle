package com.exyui.android.debugbottle.components.okhttp

import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.exyui.android.debugbottle.components.DTSettings
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuriel on 8/24/16.
 */
internal object HttpBlockFileMgr {
    private val TYPE = ".txt"
    private val TAG = "HttpBlockFileMgr"

    private val SAVE_DELETE_LOCK = Any()
    private val FILE_NAME_FORMATTER = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.getDefault())
    private val TIME_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val OBSOLETE_DURATION = 2 * 24 * 3600 * 1000L

    internal val path: String
        get() {
            val state = Environment.getExternalStorageState()
            val logPath = DTSettings.getHttpFileStorePath()

            if (Environment.MEDIA_MOUNTED == state && Environment.getExternalStorageDirectory().canWrite()) {
                return Environment.getExternalStorageDirectory().path + logPath
            }
            return Environment.getDataDirectory().absolutePath + logPath
        }

    internal fun detectedHttpBlockDirectory(): File {
        val directory = File(path)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    internal val logFiles: Array<File>?
        get() {
            val f = detectedHttpBlockDirectory()
            if (f.exists() && f.isDirectory) {
                return f.listFiles() { dir, filename ->
                    filename.endsWith(TYPE)
                }
            }
            return null
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
        val handlerThread = HandlerThread("DT_http_listener")
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

    /**
     * Save log to file

     * @param str block log string
     * *
     * @return log file path
     */
    fun saveHttpLog(str: String): String {
        var path: String = ""
        synchronized(SAVE_DELETE_LOCK) {
            path = saveLogToSDCard("http", str)
        }
        return path
    }

    private fun saveLogToSDCard(logFileName: String, str: String): String {
        var path = ""
        var writer: BufferedWriter? = null
        try {
            val file = detectedHttpBlockDirectory()
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

}