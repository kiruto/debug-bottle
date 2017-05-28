package com.exyui.android.debugbottle.components.crash

import android.util.Log
import com.exyui.android.debugbottle.components.okhttp.HttpBlock
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuriel on 9/13/16.
 */
internal data class CrashBlock(
        val time: String = "",
        val threadName: String = "",
        val threadId: String = "",
        val threadPriority: String = "",
        val threadGroup: String = "",
        val message: String = "",
        val cause: String = "",
        val stacktrace: String = "",
        val file: File? = null) {

    private val threadSb = StringBuilder()
    private val stacktraceSb = StringBuilder()

    val threadString: String
        get() = threadSb.toString()

    val stacktraceString: String
        get() = stacktraceSb.toString()

    init {
        flushString()
    }

    private fun flushString() {
        threadSb + KEY_TIME + KV + time + SEPARATOR
        threadSb + KEY_THREAD_NAME + KV + threadName + SEPARATOR
        threadSb + KEY_THREAD_ID + KV + threadId + SEPARATOR
        threadSb + KEY_THREAD_PRIORITY + KV + threadPriority + SEPARATOR
        threadSb + KEY_THREAD_GROUP + KV + threadGroup + SEPARATOR

        stacktraceSb + KEY_MESSAGE + KV + message + SEPARATOR
        if (cause.isNotEmpty()) {
            stacktraceSb + KEY_CAUSE + KV + cause + SEPARATOR
        }
        stacktraceSb + KEY_STACKTRACE + KV + stacktrace + SEPARATOR
    }

    private operator fun StringBuilder.plus(any: Any): StringBuilder = this.append(any)

    override fun toString(): String {
        return threadSb.toString() + stacktraceSb
    }

    companion object {
        internal val TAG = "CrashBlock"
        internal val NEW_INSTANCE = "newInstance: "

        internal val SEPARATOR = "\r\n"
        internal val KV = " = "

        internal val KEY_TIME = "time"
        internal val KEY_THREAD_NAME = "threadName"
        internal val KEY_THREAD_ID = "threadId"
        internal val KEY_THREAD_PRIORITY = "threadPriority"
        internal val KEY_THREAD_GROUP = "threadGroup"
        internal val KEY_MESSAGE = "message"
        internal val KEY_CAUSE = "cause"
        internal val KEY_STACKTRACE = "stacktrace"

        private val TIME_FORMATTER = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault())

        fun newInstance(thread: Thread, ex: Throwable): CrashBlock {
            val writer = StringWriter()
            val printer = PrintWriter(writer)
            ex.printStackTrace(printer)

            return CrashBlock(
                    time = TIME_FORMATTER.format(System.currentTimeMillis()),
                    threadId = "${thread.id}",
                    threadName = thread.name,
                    threadPriority = "${thread.priority}",
                    threadGroup = thread.threadGroup.name,
                    message = ex.message?: "",
                    cause = ex.cause?.message?: "",
                    stacktrace = writer.toString()
            )
        }

        fun newInstance(file: File): CrashBlock {
            var reader: BufferedReader? = null

            var time: String = ""
            var threadName: String = ""
            var threadId: String = ""
            var threadPriority: String = ""
            var threadGroup: String = ""
            var message: String = ""
            var cause: String = ""
            var stacktrace: String = ""

            try {
                val input = InputStreamReader(FileInputStream(file), "UTF-8")

                reader = BufferedReader(input)
                var line: String? = reader.readLine()

                fun getStringInfo(): String {
                    if (null == line || null == reader)
                        return ""
                    val split = line!!.split(HttpBlock.KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (split.size > 1) {
                        val sb = StringBuilder(split[1])
                        sb/*.append(line!!.getValue())*/.append(HttpBlock.SEPARATOR)
                        line = reader!!.readLine()

                        // read until SEPARATOR appears
                        while (line != null) {
                            if (line != "") {
                                sb.append(line).append(HttpBlock.SEPARATOR)
                            } else {
                                break
                            }
                            line = reader!!.readLine()
                        }
                        return sb.toString()
                    } else
                        return ""
                }

                while(line != null) {
                    if (line!!.startsWith(KEY_TIME)) {
                        time = line!!.getValue()
                    } else if (line!!.startsWith(KEY_THREAD_NAME)) {
                        threadName = line!!.getValue()
                    } else if (line!!.startsWith(KEY_THREAD_ID)) {
                        threadId = line!!.getValue()
                    } else if (line!!.startsWith(KEY_THREAD_PRIORITY)) {
                        threadPriority = line!!.getValue()
                    } else if (line!!.startsWith(KEY_THREAD_GROUP)) {
                        threadGroup = line!!.getValue()
                    } else if (line!!.startsWith(KEY_MESSAGE)) {
                        message = line!!.getValue()
                    } else if (line!!.startsWith(KEY_CAUSE)) {
                        cause = line!!.getValue()
                    } else if (line!!.startsWith(KEY_STACKTRACE)) {
                        stacktrace = getStringInfo()
                    }
                    line = reader.readLine()
                }
                reader.close()
                reader = null
            } catch(e: Exception) {
                Log.e(TAG, NEW_INSTANCE, e)
            } finally {
                try {
                    if (reader != null) {
                        reader.close()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, NEW_INSTANCE, e)
                }

            }
            return CrashBlock(
                    time = time,
                    threadName = threadName,
                    threadId = threadId,
                    threadPriority = threadPriority,
                    threadGroup = threadGroup,
                    message = message,
                    cause = cause,
                    stacktrace = stacktrace,
                    file = file
            )
        }

        private fun String.getValue(): String {
            val kv = this.split(KV.toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
            if (kv.size > 1) {
                return kv[1]
            } else {
                return ""
            }
        }
    }
}