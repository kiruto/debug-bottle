package me.chunyu.yuriel.kotdebugtool.core.log

import android.os.Environment
import me.chunyu.yuriel.kotdebugtool.core.CanaryCore
import me.chunyu.yuriel.kotdebugtool.core.CanaryCoreMgr
import java.io.File

/**
 * Created by yuriel on 8/8/16.
 */
object BlockCanaryInternals {
    private val TYPE = ".txt"

    internal val path: String
        get() {
            val state = Environment.getExternalStorageState()
            val logPath = if (CanaryCoreMgr.context == null) "" else CanaryCoreMgr.context?.logPath

            if (Environment.MEDIA_MOUNTED == state && Environment.getExternalStorageDirectory().canWrite()) {
                return Environment.getExternalStorageDirectory().path + logPath
            }
            return Environment.getDataDirectory().absolutePath + logPath
        }

    internal fun detectedBlockDirectory(): File {
        val directory = File(path)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    val logFiles: Array<File>?
        get() {
            val f = detectedBlockDirectory()
            if (f.exists() && f.isDirectory) {
                return f.listFiles() { dir, filename ->
                    filename.endsWith(TYPE)
                }
            }
            return null
        }
}