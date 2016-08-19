package me.chunyu.yuriel.kotdebugtool.core.log

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import me.chunyu.yuriel.kotdebugtool.core.CanaryCore
import me.chunyu.yuriel.kotdebugtool.core.CanaryCoreMgr
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuriel on 8/8/16.
 */
class Block private constructor() {

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
    var threadStackEntries: ArrayList<String>? = ArrayList()
        private set
    var logFile: File? = null
        private set

    private val basicSb = StringBuilder()
    private val cpuSb = StringBuilder()
    private val timeSb = StringBuilder()
    private val stackSb = StringBuilder()

    companion object {

        private val TAG = "Block"

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

        fun newInstance(): Block {
            val block = Block()
            val context = CanaryCoreMgr.context?.context
            if (block.versionName == null || block.versionName!!.length == 0) {
                try {
                    val info = context?.packageManager?.getPackageInfo(context.packageName, 0)
                    block.versionCode = info?.versionCode?: 0
                    block.versionName = info?.versionName
                } catch (e: Throwable) {
                    Log.e(TAG, NEW_INSTANCE, e)
                }

            }

            if (block.imei == null || block.imei!!.length == 0) {
                try {
                    block.imei = (context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
                } catch (e: Exception) {
                    Log.e(TAG, NEW_INSTANCE, e)
                    block.imei = EMPTY_IMEI
                }

            }
            block.qualifier = CanaryCoreMgr.context?.qualifier?: ""
            block.apiLevel = "${Build.VERSION.SDK_INT} ${Build.VERSION.RELEASE}"
            block.model = Build.MODEL
            block.uid = CanaryCoreMgr.context?.uid?: ""
            block.cpuCoreNum = PerformanceUtils.numCores
            block.processName = ProcessUtils.myProcessName()
            block.network = CanaryCoreMgr.context?.networkType?: ""
            block.freeMemory = PerformanceUtils.freeMemory.toString()
            block.totalMemory = PerformanceUtils.totalMemory.toString()
            return block
        }

        /**
         * Create [Block] from saved log file.

         * @param file looper log file
         * *
         * @return LooperLog created from log file
         */
        fun newInstance(file: File): Block {
            val block = Block()
            block.logFile = file

            var reader: BufferedReader? = null
            try {
                val `in` = InputStreamReader(FileInputStream(file), "UTF-8")

                reader = BufferedReader(`in`)
                var line: String? = reader.readLine()
                while (line != null) {
                    if (line.startsWith(KEY_QUA)) {
                        block.qualifier = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_MODEL)) {
                        block.model = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_API)) {
                        block.apiLevel = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_IMEI)) {
                        block.imei = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_UID)) {
                        block.uid = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_CPU_CORE)) {
                        block.cpuCoreNum = Integer.valueOf(line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])!!
                    } else if (line.startsWith(KEY_PROCESS_NAME)) {
                        block.processName = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_VERSION_NAME)) {
                        block.versionName = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_VERSION_CODE)) {
                        block.versionCode = Integer.valueOf(line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])!!
                    } else if (line.startsWith(KEY_NETWORK)) {
                        block.network = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_TOTAL_MEMORY)) {
                        block.totalMemory = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_FREE_MEMORY)) {
                        block.freeMemory = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_CPU_BUSY)) {
                        block.cpuBusy = java.lang.Boolean.valueOf(line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])!!
                    } else if (line.startsWith(KEY_CPU_RATE)) {
                        val split = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (split.size > 1) {
                            val cpuRateSb = StringBuilder(split[1])
                            cpuRateSb.append(line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]).append(SEPARATOR)
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
                        }

                    } else if (line.startsWith(KEY_TIME_COST_START)) {
                        block.timeStart = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_TIME_COST_END)) {
                        block.timeEnd = line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    } else if (line.startsWith(KEY_TIME_COST)) {
                        block.timeCost = java.lang.Long.valueOf(line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])!!
                    } else if (line.startsWith(KEY_THREAD_TIME_COST)) {
                        block.threadTimeCost = java.lang.Long.valueOf(line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])!!
                    } else if (line.startsWith(KEY_STACK)) {
                        var stackSb = StringBuilder(line.split(KV.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
                        line = reader.readLine()

                        // read until file ends
                        while (line != null) {
                            if (line != "") {
                                stackSb.append(line).append(SEPARATOR)
                            } else if (stackSb.length > 0) {
                                // ignore continual blank lines
                                block.threadStackEntries!!.add(stackSb.toString())
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
                        reader = null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, NEW_INSTANCE, e)
                }

            }
            block.flushString()
            return block
        }
    }

    fun setCpuBusyFlag(busy: Boolean): Block {
        cpuBusy = busy
        return this
    }

    fun setRecentCpuRate(info: String): Block {
        cpuRateInfo = info
        return this
    }

    fun setThreadStackEntries(threadStackEntries: ArrayList<String>): Block {
        this.threadStackEntries = threadStackEntries
        return this
    }

    fun setMainThreadTimeCost(realTimeStart: Long, realTimeEnd: Long, threadTimeStart: Long, threadTimeEnd: Long): Block {
        timeCost = realTimeEnd - realTimeStart
        threadTimeCost = threadTimeEnd - threadTimeStart
        timeStart = TIME_FORMATTER.format(realTimeStart)
        timeEnd = TIME_FORMATTER.format(realTimeEnd)
        return this
    }

    fun flushString(): Block {
        val separator = SEPARATOR
        basicSb.append(KEY_QUA).append(KV).append(qualifier).append(separator)
        basicSb.append(KEY_VERSION_NAME).append(KV).append(versionName).append(separator)
        basicSb.append(KEY_VERSION_CODE).append(KV).append(versionCode).append(separator)
        basicSb.append(KEY_IMEI).append(KV).append(imei).append(separator)
        basicSb.append(KEY_UID).append(KV).append(uid).append(separator)
        basicSb.append(KEY_NETWORK).append(KV).append(network).append(separator)
        basicSb.append(KEY_MODEL).append(KV).append(Build.MODEL).append(separator)
        basicSb.append(KEY_API).append(KV).append(apiLevel).append(separator)
        basicSb.append(KEY_CPU_CORE).append(KV).append(cpuCoreNum).append(separator)
        basicSb.append(KEY_PROCESS_NAME).append(KV).append(processName).append(separator)
        basicSb.append(KEY_FREE_MEMORY).append(KV).append(freeMemory).append(separator)
        basicSb.append(KEY_TOTAL_MEMORY).append(KV).append(totalMemory).append(separator)

        timeSb.append(KEY_TIME_COST).append(KV).append(timeCost).append(separator)
        timeSb.append(KEY_THREAD_TIME_COST).append(KV).append(threadTimeCost).append(separator)
        timeSb.append(KEY_TIME_COST_START).append(KV).append(timeStart).append(separator)
        timeSb.append(KEY_TIME_COST_END).append(KV).append(timeEnd).append(separator)

        cpuSb.append(KEY_CPU_BUSY).append(KV).append(cpuBusy).append(separator)
        cpuSb.append(KEY_CPU_RATE).append(KV).append(cpuRateInfo).append(separator)

        if (threadStackEntries != null && !threadStackEntries!!.isEmpty()) {
            val temp = StringBuilder()
            for (s in threadStackEntries!!) {
                temp.append(s)
                temp.append(separator)
            }
            stackSb.append(KEY_STACK).append(KV).append(temp.toString()).append(separator)
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
            for (stackEntry in threadStackEntries!!) {

                if (Character.isLetter(stackEntry[0])) {
                    val lines = stackEntry.split(Block.SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (line in lines) {
                        if (!line.startsWith("com.android") && !line.startsWith("java") && !line.startsWith("android")) {
                            result = line.substring(line.indexOf('(') + 1, line.indexOf(')'))
                            return result
                        }
                    }
                }
            }
            return result
        }

    override fun toString(): String {
        return basicSb.toString() + timeSb + cpuSb + stackSb
    }
}
