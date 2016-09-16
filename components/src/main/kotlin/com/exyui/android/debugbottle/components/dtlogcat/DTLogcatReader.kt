package com.exyui.android.debugbottle.components.dtlogcat

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by yuriel on 9/17/16.
 */
internal object DTLogcatReader {
    private val TAG = "DTLogcatReader"
    private val processId = android.os.Process.myPid().toString()

    val log: StringBuilder
        get() {

            val builder = StringBuilder()

            try {
                val command = arrayOf("logcat", "-d", "-v", "threadtime")

                val process = Runtime.getRuntime().exec(command)

                val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

                var line: String? = bufferedReader.readLine()
                while (line != null) {
                    if (line.contains(processId)) {
                        builder.append(line)
                    }
                    line = bufferedReader.readLine()
                }
            } catch (ex: IOException) {
                Log.e(TAG, "getLog failed", ex)
            }

            return builder
        }
}