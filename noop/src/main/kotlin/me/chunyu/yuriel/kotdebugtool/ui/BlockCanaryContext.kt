package me.chunyu.yuriel.kotdebugtool.ui

import android.content.Context
import java.io.File

/**
 * Created by yuriel on 8/30/16.
 */
open class BlockCanaryContext(any: Any) {

    open val configDuration: Int = 99999

    open val configBlockThreshold: Long = 0L

    open val isNeedDisplay: Boolean = false

    open val qualifier: String = ""

    open val uid: String = ""

    open val networkType: String = ""

    open val context: Context? = null

    open val logPath: String = ""

    open fun zipLogFile(src: Array<File>?, dest: File): Boolean = false

    open fun uploadLogFile(zippedFile: File) {}

    open val stackFoldPrefix: String? = null

    open val configDumpIntervalMillis: Long = 0L
}