package me.chunyu.yuriel.kotdebugtool.core

import android.os.Looper
import android.util.Log
import android.util.Printer
import me.chunyu.yuriel.kotdebugtool.core.log.Block

/**
 * Created by yuriel on 8/8/16.
 */
object CanaryCore {

    private val context = CanaryCoreMgr.context
    internal var mainLooperPrinter: LooperPrinter
    internal var threadStackSampler: ThreadStackSampler?
    internal var cpuSampler: CpuSampler

    var mOnBlockEventInterceptor: OnBlockEventInterceptor? = null

    init {

        threadStackSampler = ThreadStackSampler(Looper.getMainLooper().thread,
                context?.configDumpIntervalMillis?: 0L)
        cpuSampler = CpuSampler(context?.configDumpIntervalMillis?: 0L)

        val threshold = context?.configBlockThreshold?: LooperPrinter.DEFAULT_BLOCK_THRESHOLD_MILLIS
        mainLooperPrinter = LooperPrinter(threshold) { realTimeStart, realTimeEnd, threadTimeStart, threadTimeEnd ->

            // Get recent thread-stack entries and cpu usage
            if (null == threadStackSampler) return@LooperPrinter

            val threadStackEntries = threadStackSampler!!.getThreadStackEntries(realTimeStart, realTimeEnd)
            // Log.d("BlockCanary", "threadStackEntries: " + threadStackEntries.size)
            if (!threadStackEntries.isEmpty()) {
                val block = Block.newInstance()
                        .setMainThreadTimeCost(realTimeStart, realTimeEnd, threadTimeStart, threadTimeEnd)
                        .setCpuBusyFlag(cpuSampler.isCpuBusy(realTimeStart, realTimeEnd))
                        .setRecentCpuRate(cpuSampler.cpuRateInfo)
                        .setThreadStackEntries(threadStackEntries)
                        .flushString()
                LogWriter.saveLooperLog(block.toString())

                if (context?.isNeedDisplay?: false && mOnBlockEventInterceptor != null) {
                    mOnBlockEventInterceptor?.onBlockEvent(context?.context!!, block.timeStart)
                }
            }
        }
        LogWriter.cleanOldFiles()
    }

    /**
     * Because postDelayed maybe cause more delay than MonitorEnv.get().getConfigBlockThreshold()
     * it maybe cause can not get Thread Task info when notify block event.
     * So, is it be better to make delay time < MonitorEnv.get().getConfigBlockThreshold(),
     * such as MonitorEnv.get().getConfigBlockThreshold() * 0.8f OR MonitorEnv.get().getConfigBlockThreshold() - 20 ?

     * @return sample delay
     */
    internal val sampleDelay: Long = (0.8F * (context?.configBlockThreshold?: 0L)).toLong()

    fun stopSample() {
        threadStackSampler?.stop()
        cpuSampler.stop()
    }

    fun getPrinter(): Printer = mainLooperPrinter
}
