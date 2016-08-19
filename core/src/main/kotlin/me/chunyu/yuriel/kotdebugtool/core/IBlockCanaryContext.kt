package me.chunyu.yuriel.kotdebugtool.core

import android.content.Context
import java.io.File

/**
 * Created by yuriel on 8/8/16.
 */
interface IBlockCanaryContext {

    val configBlockThreshold: Long

    val isNeedDisplay: Boolean

    val qualifier: String

    val uid: String

    val networkType: String

    val context: Context

    val logPath: String

    fun zipLogFile(src: Array<File>?, dest: File): Boolean

    fun uploadLogFile(zippedFile: File)

    val stackFoldPrefix: String?

    val configDumpIntervalMillis: Long
}
