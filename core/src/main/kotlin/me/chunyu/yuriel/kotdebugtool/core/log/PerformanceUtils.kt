package me.chunyu.yuriel.kotdebugtool.core.log

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import me.chunyu.yuriel.kotdebugtool.core.CanaryCore
import me.chunyu.yuriel.kotdebugtool.core.CanaryCoreMgr
import java.io.*
import java.util.regex.Pattern

/**
 * Created by yuriel on 8/8/16.
 */
internal object PerformanceUtils {
    private val TAG = "PerformanceUtils"

    private var sCoreNum = 0
    private var sTotalMemo: Long = 0

    /**
     * Get cpu core number

     * @return int cpu core number
     */
    // Get directory containing CPU info
    // Filter to only list the devices we care about
    // Return the number of cores (virtual CPU devices)
    val numCores: Int
        get() {
            class CpuFilter : FileFilter {
                override fun accept(pathname: File): Boolean {
                    return Pattern.matches("cpu[0-9]", pathname.name)
                }
            }

            if (sCoreNum == 0) {
                try {
                    val dir = File("/sys/devices/system/cpu/")
                    val files = dir.listFiles(CpuFilter())
                    sCoreNum = files.size
                } catch (e: Exception) {
                    Log.e(TAG, "getNumCores exception", e)
                    sCoreNum = 1
                }

            }
            return sCoreNum
        }

    val freeMemory: Long
        get() {
            val mi = ActivityManager.MemoryInfo()
            try {
                (CanaryCoreMgr.context?.context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?)?.getMemoryInfo(mi)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return mi.availMem / 1024
        }

    val totalMemory: Long
        get() {
            if (sTotalMemo == 0L) {
                val str1 = "/proc/meminfo"
                val str2: String?
                val arrayOfString: Array<String>
                var initial_memory: Long = -1
                var localFileReader: FileReader? = null
                try {
                    localFileReader = FileReader(str1)
                    val localBufferedReader = BufferedReader(localFileReader, 8192)
                    str2 = localBufferedReader.readLine()

                    if (str2 != null) {
                        arrayOfString = str2.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        initial_memory = Integer.valueOf(arrayOfString[1])!!.toLong()
                    }
                    localBufferedReader.close()

                } catch (e: IOException) {
                    Log.e(TAG, "getTotalMemory exception = ", e)
                } finally {
                    if (localFileReader != null) {
                        try {
                            localFileReader.close()
                        } catch (e: IOException) {
                            Log.e(TAG, "close localFileReader exception = ", e)
                        }

                    }
                }
                sTotalMemo = initial_memory
            }
            return sTotalMemo
        }
}
