package com.exyui.android.debugbottle.core.log

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.exyui.android.debugbottle.core.__CanaryCore
import com.exyui.android.debugbottle.core.__CanaryCoreMgr
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuriel on 8/8/16.
 */
class __Block private constructor() {

    var qualifier: String = ""
        private set
    var model: String = ""
        private set
    var apiLevel = ""
        private set
    var imei: String? = ""
        private set
    var uid: String = ""
        private set
    var cpuCoreNum: Int = 0
        private set
    var processName: String = ""
        private set
    var versionName: String? = ""
        private set
    var versionCode: Int = 0
        private set
    var network: String = ""
        private set
    var freeMemory: String = ""
        private set
    var totalMemory: String = ""
        private set
    var timeCost: Long = 0
        private set
    var threadTimeCost: Long = 0
        private set
    var timeStart: String = ""
        private set
    var timeEnd: String = ""
        private set
    var cpuBusy: Boolean = false
        private set
    var cpuRateInfo: String = ""
        private set
    var threadStackEntries: ArrayList<String> = ArrayList()
        private set
    var logFile: File? = null
        private set

    private val basicSb = StringBuilder()
    private val cpuSb = StringBuilder()
    private val timeSb = StringBuilder()
    private val stackSb = StringBuilder()

    companion object {

        private val TAG = "__Block"

        val SEPARATOR = "\r\n"
        val KV = " = "

        val KEY_QUA = "qualifier"
        val KEY_MODEL = "model"
        val KEY_API = "apilevel"
        val KEY_IMEI = "imei"
        val KEY_UID = "uid"
        val KEY_CPU_CORE = "cpuCore"
        val KEY_CPU_BUSY = "cpubusy"
        val KEY_CPU_RATE = "cpurate"
        val KEY_TIME_COST = "timecost"
        val KEY_THREAD_TIME_COST = "threadtimecost"
        val KEY_TIME_COST_START = "timestart"
        val KEY_TIME_COST_END = "timeend"
        val KEY_STACK = "stack"
        val KEY_PROCESS_NAME = "processName"
        val KEY_VERSION_NAME = "versionName"
        val KEY_VERSION_CODE = "versionCode"
        val KEY_NETWORK = "network"
        val KEY_TOTAL_MEMORY = "totalMemory"
        val KEY_FREE_MEMORY = "freeMemory"
        val NEW_INSTANCE = "newInstance: "
        private val TIME_FORMATTER = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault())
        private val EMPTY_IMEI = "empty_imei"

        fun newInstance(): __Block {
            return __Block().apply {
                val context = __CanaryCoreMgr.context?.context
                if (versionName?.isEmpty()?: false) {
                    try {
                        val info = context?.packageManager?.getPackageInfo(context.packageName, 0)
                        versionCode = info?.versionCode?: 0
                        versionName = info?.versionName
                    } catch (e: Throwable) {
                        Log.e(TAG, NEW_INSTANCE, e)
                    }

                }

                if (imei?.isEmpty()?: false) {
                    try {
                        imei = (context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
                    } catch (e: Exception) {
                        Log.e(TAG, NEW_INSTANCE, e)
                        imei = EMPTY_IMEI
                    }

                }
                qualifier = __CanaryCoreMgr.context?.qualifier?: ""
                apiLevel = "${Build.VERSION.SDK_INT} ${Build.VERSION.RELEASE}"
                model = Build.MODEL
                uid = __CanaryCoreMgr.context?.uid?: ""
                cpuCoreNum = PerformanceUtils.numCores
                processName = __ProcessUtils.myProcessName()
                network = __CanaryCoreMgr.context?.networkType?: ""
                freeMemory = PerformanceUtils.freeMemory.toString()
                totalMemory = PerformanceUtils.totalMemory.toString()
            }
        }

        /**
         * Create [Block] from saved log file.

         * @param file looper log file
         * *
         * @return LooperLog created from log file
         */
        fun newInstance(file: File): __Block {
            val block = __Block()
            block.logFile = file

            var reader: BufferedReader? = null
            try {
                val input = InputStreamReader(FileInputStream(file), "UTF-8")

                reader = BufferedReader(input)
                var line: String? = reader.readLine()
                while (line != null) {
                    if (line.startsWith(KEY_QUA)) {
                        block.qualifier = line.getValue()
                    } else if (line.startsWith(KEY_MODEL)) {
                        block.model = line.getValue()
                    } else if (line.startsWith(KEY_API)) {
                        block.apiLevel = line.getValue()
                    } else if (line.startsWith(KEY_IMEI)) {
                        block.imei = line.getValue()
                    } else if (line.startsWith(KEY_UID)) {
                        block.uid = line.getValue()
                    } else if (line.startsWith(KEY_CPU_CORE)) {
                        block.cpuCoreNum = Integer.valueOf(line.getValue())!!
                    } else if (line.startsWith(KEY_PROCESS_NAME)) {
                        block.processName = line.getValue()
                    } else if (line.startsWith(KEY_VERSION_NAME)) {
                        block.versionName = line.getValue()
                    } else if (line.startsWith(KEY_VERSION_CODE)) {
                        block.versionCode = Integer.valueOf(line.getValue())!!
                    } else if (line.startsWith(KEY_NETWORK)) {
                        block.network = line.getValue()
                    } else if (line.startsWith(KEY_TOTAL_MEMORY)) {
                        block.totalMemory = line.getValue()
                    } else if (line.startsWith(KEY_FREE_MEMORY)) {
                        block.freeMemory = line.getValue()
                    } else if (line.startsWith(KEY_CPU_BUSY)) {
                        block.cpuBusy = java.lang.Boolean.valueOf(line.getValue())!!
                    } else if (line.startsWith(KEY_CPU_RATE)) {
                        val split = line.split(KV.toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
                        if (split.size > 1) {
                            val cpuRateSb = StringBuilder(split[1])
                            cpuRateSb.append(line.getValue()).append(SEPARATOR)
                            line = reader.readLine()

                            // read until SEPARATOR appears
                            while (line != null) {
                                if (line != "") {
                                    cpuRateSb.append(line).append(SEPARATOR)
                                } else {
                                    break
                                }
                                line = reader.readLine()
                            }
                            block.cpuRateInfo = cpuRateSb.toString()
                        } else {
                            block.cpuRateInfo = "Not detected."
                        }

                    } else if (line.startsWith(KEY_TIME_COST_START)) {
                        block.timeStart = line.getValue()
                    } else if (line.startsWith(KEY_TIME_COST_END)) {
                        block.timeEnd = line.getValue()
                    } else if (line.startsWith(KEY_TIME_COST)) {
                        block.timeCost = java.lang.Long.valueOf(line.getValue())!!
                    } else if (line.startsWith(KEY_THREAD_TIME_COST)) {
                        block.threadTimeCost = java.lang.Long.valueOf(line.getValue())!!
                    } else if (line.startsWith(KEY_STACK)) {
                        var stackSb = StringBuilder(line.getValue())
                        line = reader.readLine()

                        // read until file ends
                        while (line != null) {
                            if (line != "") {
                                stackSb.append(line).append(SEPARATOR)
                            } else if (stackSb.length > 0) {
                                // ignore continual blank lines
                                block.threadStackEntries.add(stackSb.toString())
                                stackSb = StringBuilder()
                            }
                            line = reader.readLine()
                        }
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
            block.flushString()
            return block
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

    fun setCpuBusyFlag(busy: Boolean): __Block {
        cpuBusy = busy
        return this
    }

    fun setRecentCpuRate(info: String): __Block {
        cpuRateInfo = info
        return this
    }

    fun setThreadStackEntries(threadStackEntries: ArrayList<String>): __Block {
        this.threadStackEntries = threadStackEntries
        return this
    }

    fun setMainThreadTimeCost(realTimeStart: Long, realTimeEnd: Long, threadTimeStart: Long, threadTimeEnd: Long): __Block {
        timeCost = realTimeEnd - realTimeStart
        threadTimeCost = threadTimeEnd - threadTimeStart
        timeStart = TIME_FORMATTER.format(realTimeStart)
        timeEnd = TIME_FORMATTER.format(realTimeEnd)
        return this
    }

    fun flushString(): __Block {
        basicSb + KEY_QUA + KV + qualifier + SEPARATOR
        basicSb + KEY_VERSION_NAME + KV + versionName + SEPARATOR
        basicSb + KEY_VERSION_CODE + KV + versionCode + SEPARATOR
        basicSb + KEY_IMEI + KV + imei + SEPARATOR
        basicSb + KEY_UID + KV + uid + SEPARATOR
        basicSb + KEY_NETWORK + KV + network + SEPARATOR
        basicSb + KEY_MODEL + KV + Build.MODEL + SEPARATOR
        basicSb + KEY_API + KV + apiLevel + SEPARATOR
        basicSb + KEY_CPU_CORE + KV + cpuCoreNum + SEPARATOR
        basicSb + KEY_PROCESS_NAME + KV + processName + SEPARATOR
        basicSb + KEY_FREE_MEMORY + KV + freeMemory + SEPARATOR
        basicSb + KEY_TOTAL_MEMORY + KV + totalMemory + SEPARATOR

        timeSb + KEY_TIME_COST + KV + timeCost + SEPARATOR
        timeSb + KEY_THREAD_TIME_COST + KV + threadTimeCost + SEPARATOR
        timeSb + KEY_TIME_COST_START + KV + timeStart + SEPARATOR
        timeSb + KEY_TIME_COST_END + KV + timeEnd + SEPARATOR

        cpuSb + KEY_CPU_BUSY + KV + cpuBusy + SEPARATOR
        cpuSb + KEY_CPU_RATE + KV + cpuRateInfo + SEPARATOR

        if (threadStackEntries.isNotEmpty()) {
            val temp = StringBuilder()
            for (s in threadStackEntries) {
                temp + s
                temp + SEPARATOR
            }
            stackSb + KEY_STACK + KV + temp.toString() + SEPARATOR
        }
        return this
    }

    val basicString: String
        get() = basicSb.toString()

    val cpuString: String
        get() = cpuSb.toString()

    val timeString: String
        get() = timeSb.toString()

    val keyStackString: String
        get() {
            var result = ""
            threadStackEntries
                    .asSequence()
                    .filter { Character.isLetter(it[0]) }
                    .map { it.split(__Block.SEPARATOR.toRegex()).dropLastWhile(String::isEmpty).toTypedArray() }
                    .forEach {
                        for (line in it) {
                            if (!line.startsWith("com.android") && !line.startsWith("java") && !line.startsWith("android")) {
                                result = line.substring(line.indexOf('(') + 1, line.indexOf(')'))
                                return result
                            }
                        }
                    }
            return result
        }

    override fun toString(): String = basicSb.toString() + timeSb + cpuSb + stackSb

    private operator fun StringBuilder.plus(any: Any?): StringBuilder = this.append(any?: "")

}
