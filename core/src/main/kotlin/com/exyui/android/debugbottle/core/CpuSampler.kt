package com.exyui.android.debugbottle.core

import android.util.Log
import com.exyui.android.debugbottle.core.log.Block
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuriel on 8/8/16.
 */
internal class CpuSampler(sampleIntervalMillis: Long) : Sampler(sampleIntervalMillis) {

    /**
     * TODO: Explain how we define cpu busy in README
     */
    private val BUSY_TIME: Int

    private val mCpuInfoEntries = LinkedHashMap<Long, String>()
    private var mPid = 0
    private var mUserLast: Long = 0
    private var mSystemLast: Long = 0
    private var mIdleLast: Long = 0
    private var mIoWaitLast: Long = 0
    private var mTotalLast: Long = 0
    private var mAppCpuTimeLast: Long = 0

    init {
        BUSY_TIME = (mSampleIntervalMillis * 1.2f).toInt()
    }

    override fun start() {
        super.start()
        reset()
    }

    /**
     * Get cpu rate information

     * @return string show cpu rate information
     */
    val cpuRateInfo: String
        get() {
            val sb = StringBuilder()
            val dateFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS")
            synchronized(mCpuInfoEntries) {
                for ((time, value) in mCpuInfoEntries) {
                    sb.append(dateFormat.format(time)).append(' ').append(value).append(Block.SEPARATOR)
                }
            }
            return sb.toString()
        }

    fun isCpuBusy(start: Long, end: Long): Boolean {
        if (end - start > mSampleIntervalMillis) {
            val s = start - mSampleIntervalMillis
            val e = start + mSampleIntervalMillis
            var last: Long = 0
            synchronized(mCpuInfoEntries) {
                for ((time) in mCpuInfoEntries) {
                    if (s < time && time < e) {
                        if (last != 0L && time - last > BUSY_TIME) {
                            return true
                        }
                        last = time
                    }
                }
            }
        }
        return false
    }

    override fun doSample() {
        var cpuReader: BufferedReader? = null
        var pidReader: BufferedReader? = null
        try {
            cpuReader = BufferedReader(InputStreamReader(
                    FileInputStream("/proc/stat")), 1000)
            var cpuRate: String? = cpuReader.readLine()
            if (cpuRate == null) {
                cpuRate = ""
            }

            if (mPid == 0) {
                mPid = android.os.Process.myPid()
            }
            pidReader = BufferedReader(InputStreamReader(
                    FileInputStream("/proc/$mPid/stat")), 1000)
            var pidCpuRate: String? = pidReader.readLine()
            if (pidCpuRate == null) {
                pidCpuRate = ""
            }

            parseCpuRate(cpuRate, pidCpuRate)
        } catch (ex: Throwable) {
            Log.e(TAG, "doSample: ", ex)
        } finally {
            try {
                if (cpuReader != null) {
                    cpuReader.close()
                }
                if (pidReader != null) {
                    pidReader.close()
                }
            } catch (e: IOException) {
                Log.e(TAG, "doSample: ", e)
            }

        }
    }

    private fun reset() {
        mUserLast = 0
        mSystemLast = 0
        mIdleLast = 0
        mIoWaitLast = 0
        mTotalLast = 0
        mAppCpuTimeLast = 0
    }

    private fun parseCpuRate(cpuRate: String, pidCpuRate: String) {
        val cpuInfoArray = cpuRate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (cpuInfoArray.size < 9) {
            return
        }
        // 从系统启动开始累计到当前时刻，用户态的CPU时间，不包含 nice值为负进程
        val user = java.lang.Long.parseLong(cpuInfoArray[2])
        // 从系统启动开始累计到当前时刻，nice值为负的进程所占用的CPU时间
        val nice = java.lang.Long.parseLong(cpuInfoArray[3])
        // 从系统启动开始累计到当前时刻，核心时间
        val system = java.lang.Long.parseLong(cpuInfoArray[4])
        // 从系统启动开始累计到当前时刻，除硬盘IO等待时间以外其它等待时间
        val idle = java.lang.Long.parseLong(cpuInfoArray[5])
        // 从系统启动开始累计到当前时刻，硬盘IO等待时间
        val ioWait = java.lang.Long.parseLong(cpuInfoArray[6])
        // CPU总时间 = 以上所有加上irq（硬中断）和softirq（软中断）的时间
        val total = user + nice + system + idle + ioWait + java.lang.Long.parseLong(cpuInfoArray[7]) + java.lang.Long.parseLong(cpuInfoArray[8])

        val pidCpuInfos = pidCpuRate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (pidCpuInfos.size < 17) {
            return
        }

        /*
         * utime  Amount of time that this process has been scheduled in user mode
         * stime  Amount of time that this process has been scheduled in kernel mode
         * cutime Amount of time that this process's waited-for children have been scheduled in user mode
         * cstime Amount of time that this process's waited-for children have been scheduled in kernel mode
         * processCpuTime = utime + stime + cutime + cstime, which includes all its threads' cpu time
         */
        val appCpuTime = java.lang.Long.parseLong(pidCpuInfos[13]) + java.lang.Long.parseLong(pidCpuInfos[14])
        +java.lang.Long.parseLong(pidCpuInfos[15]) + java.lang.Long.parseLong(pidCpuInfos[16])

        if (mTotalLast != 0L) {
            val sb = StringBuilder()
            val idleTime = idle - mIdleLast
            val totalTime = total - mTotalLast
            sb.append("cpu:").append((totalTime - idleTime) * 100L / totalTime).append("% ").append("app:").append((appCpuTime - mAppCpuTimeLast) * 100L / totalTime).append("% ").append("[").append("user:").append((user - mUserLast) * 100L / totalTime).append("% ").append("system:").append((system - mSystemLast) * 100L / totalTime).append("% ").append("ioWait:").append((ioWait - mIoWaitLast) * 100L / totalTime).append("% ]")
            synchronized(mCpuInfoEntries) {
                mCpuInfoEntries.put(System.currentTimeMillis(), sb.toString())
                if (mCpuInfoEntries.size > MAX_ENTRY_COUNT) {
                    for ((key) in mCpuInfoEntries) {
                        mCpuInfoEntries.remove(key)
                        break
                    }
                }
            }
        }
        mUserLast = user
        mSystemLast = system
        mIdleLast = idle
        mIoWaitLast = ioWait
        mTotalLast = total

        mAppCpuTimeLast = appCpuTime
    }

    companion object {

        private val TAG = "CpuSampler"
        private val MAX_ENTRY_COUNT = 10
    }
}