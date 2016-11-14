package com.exyui.android.debugbottle.components.okhttp

import android.util.Log
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import java.io.*

/**
 * Created by yuriel on 8/24/16.
 */
internal data class HttpBlock(
        val url: String = "",
        val method: String = "",
        val timeStart: Long = 0L,
        val timeEnd: Long = 0L,
        val requestHeader: String = "",
        val requestBody: String = "",
        val responseCode: String = "",
        val responseHeader: String = "",
        val responseBody: String = "",
        val file: File? = null) {

    private val overviewSb = StringBuilder()
    private val timeSb = StringBuilder()
    private val requestSb = StringBuilder()
    private val responseSb = StringBuilder()

    val overviewString: String
        get() = overviewSb.toString()

    val requestString: String
        get() = requestSb.toString()

    val responseString: String
        get() = responseSb.toString()

    val time: Double
        get() = (timeEnd - timeStart) / 1e6

    init {
        flushString()
    }

    private fun flushString() {
        val t = this.time.toString()

        overviewSb + KEY_METHOD + KV + method + SEPARATOR
        overviewSb + KEY_URL + KV + url + SEPARATOR
        overviewSb + KEY_TIME + KV + t + "ms" +  SEPARATOR

        timeSb + KEY_TIME_START + KV + timeStart + SEPARATOR
        timeSb + KEY_TIME_END + KV + timeEnd + SEPARATOR

        requestSb + KEY_REQUEST_HEADER + KV + requestHeader + SEPARATOR
        requestSb + KEY_REQUEST_BODY + KV + requestBody + SEPARATOR

        responseSb + KEY_RESPONSE_CODE + KV + responseCode + SEPARATOR
        responseSb + KEY_RESPONSE_HEADER + KV + responseHeader + SEPARATOR + SEPARATOR
        responseSb + KEY_RESPONSE_BODY + KV + responseBody + SEPARATOR
    }

    private operator fun StringBuilder.plus(any: Any): StringBuilder = this.append(any)

    override fun toString(): String {
        return overviewSb.toString() + timeSb + requestSb + responseSb
    }

    companion object {
        internal val TAG = "HttpBlock"
        internal val NEW_INSTANCE = "newInstance: "

        internal val SEPARATOR = "\r\n"
        internal val KV = " = "

        internal val KEY_URL = "url"
        internal val KEY_METHOD = "method"
        internal val KEY_TIME = "time"
        internal val KEY_TIME_START = "timeStart"
        internal val KEY_TIME_END = "timeEnd"
        internal val KEY_REQUEST_HEADER = "requestHeader"
        internal val KEY_REQUEST_BODY = "requestBody"
        internal val KEY_RESPONSE_CODE = "responseCode"
        internal val KEY_RESPONSE_HEADER = "responseHeader"
        internal val KEY_RESPONSE_BODY = "responseBody"

        // Use for write to file
        fun newInstance(url: String,
                        method: String,
                        requestHeader: String,
                        responseCode: String,
                        responseHeader:String,
                        timeStart: Long,
                        timeEnd: Long,
                        requestBody: String = "",
                        responseBody: String = ""): HttpBlock {
            val result = HttpBlock(
                    url = url,
                    method = method,
                    timeStart = timeStart,
                    timeEnd = timeEnd,
                    requestHeader = requestHeader,
                    requestBody = requestBody,
                    responseCode = responseCode,
                    responseHeader = responseHeader,
                    responseBody = responseBody
            )
//            result.flushString()
            return result
        }

        // Use for read data from file
        fun newInstance(file: File): HttpBlock {
            val result: HttpBlock
            var reader: BufferedReader? = null

            var url: String? = null
            var method: String? = null
            var timeStart: String? = "0"
            var timeEnd: String? = "0"
            var requestHeader: String? = null
            var requestBody: String? = null
            var responseCode: String? = null
            var responseHeader: String? = null
            var responseBody: String? = null

            try {
                val input = InputStreamReader(FileInputStream(file), "UTF-8")

                reader = BufferedReader(input)
                var line: String? = reader.readLine()

                fun getStringInfo(): String {
                    if (null == line || null == reader)
                        return ""
                    val split = line!!.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (split.size > 1) {
                        val sb = StringBuilder(split[1])
                        sb/*.append(line!!.getValue())*/.append(SEPARATOR)
                        line = reader!!.readLine()

                        // read until SEPARATOR appears
                        while (line != null) {
                            if (line != "") {
                                sb.append(line).append(SEPARATOR)
                            } else {
                                break
                            }
                            line = reader!!.readLine()
                        }
                        return sb.toString()
                    } else
                        return ""
                }

                while (line != null) {
                    if (line!!.startsWith(KEY_URL)) {
                        url = line!!.getValue()
                    } else if (line!!.startsWith(KEY_METHOD)) {
                        method = line!!.getValue()
                    } else if (line!!.startsWith(KEY_TIME_START)) {
                        timeStart = line!!.getValue()
                    } else if (line!!.startsWith(KEY_TIME_END)) {
                        timeEnd = line!!.getValue()
                    } else if (line!!.startsWith(KEY_REQUEST_HEADER)) {
                        //requestHeader = line.getValue()
                        requestHeader = getStringInfo()
                    } else if (line!!.startsWith(KEY_REQUEST_BODY)) {
                        //requestBody = line!!.getValue()
                        requestBody = getStringInfo()
                    } else if (line!!.startsWith(KEY_RESPONSE_CODE)) {
                        responseCode = line!!.getValue()
                    } else if (line!!.startsWith(KEY_RESPONSE_HEADER)) {
                        //responseHeader = line!!.getValue()
                        responseHeader = getStringInfo()
                    } else if (line!!.startsWith(KEY_RESPONSE_BODY)) {
                        //responseBody = line!!.getValue()
                        responseBody = getStringInfo()
                    }
                    line = reader.readLine()
                }
                reader.close()
                reader = null
            } catch (t: Throwable) {
                Log.e(TAG, NEW_INSTANCE, t)
            } finally {
                try {
                    if (reader != null) {
                        reader.close()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, NEW_INSTANCE, e)
                }

            }
            result = HttpBlock(
                    url = url?: "",
                    method = method?: "",
                    timeStart = timeStart?.toLong()?: 0L,
                    timeEnd = timeEnd?.toLong()?: 0L,
                    requestHeader = requestHeader?: "",
                    requestBody = requestBody?: "",
                    responseCode = responseCode?: "",
                    responseHeader = responseHeader?: "",
                    responseBody = responseBody?: "",
                    file = file
            )
//            result.flushString()
            return result
        }

//        private fun stringifyRequestBody(request: Request): String {
//            try {
//                val copy = request.newBuilder().build()
//                val buffer = Buffer()
//                copy.body()?.writeTo(buffer)
//                return buffer.readUtf8()
//            } catch (e: IOException) {
//                return "did not work"
//            }
//        }
        
        private fun String.getValue(): String {
            val kv = this.split(KV.toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
            if (kv.size > 1) {
                return kv[1]
            } else {
                return ""
            }
        }

//        fun stringifyResponseBody(responseBody: String): String {
//            return responseBody
//        }
    }
}