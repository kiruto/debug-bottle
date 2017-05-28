package com.exyui.android.debugbottle.core

import android.util.Log
import com.exyui.android.debugbottle.core.log.__Block
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuriel on 8/8/16.
 */
internal class ThreadStackSampler(private val mThread: Thread,
                         sampleIntervalMillis: Long,
                         private var mMaxEntryCount: Int = 10) : Sampler(sampleIntervalMillis) {

    fun getThreadStackEntries(startTime: Long, endTime: Long): ArrayList<String> {
        val dateFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return ArrayList<String>().apply {
            synchronized(mThreadStackEntries) {
                mThreadStackEntries.keys
                        .filter { it in (startTime + 1)..(endTime - 1) }
                        .mapTo(this) { dateFormat.format(it) + __Block.SEPARATOR + __Block.SEPARATOR + mThreadStackEntries[it] }
            }
        }
    }

    override fun doSample() {
        Log.d("BlockCanary", "sample thread stack: [" + mThreadStackEntries.size + ", " + mMaxEntryCount + "]")
        val stringBuilder = StringBuilder()

        // Fetch thread stack info
        for (stackTraceElement in mThread.stackTrace) {
            stringBuilder.append(stackTraceElement.toString()).append(__Block.SEPARATOR)
        }

        // Eliminate obsolete entry
        synchronized(mThreadStackEntries) {
            if (mThreadStackEntries.size == mMaxEntryCount && mMaxEntryCount > 0) {
                mThreadStackEntries.remove(mThreadStackEntries.keys.iterator().next())
            }
            mThreadStackEntries.put(System.currentTimeMillis(), stringBuilder.toString())
        }
    }

    companion object {
        private val mThreadStackEntries = LinkedHashMap<Long, String>()
    }
}