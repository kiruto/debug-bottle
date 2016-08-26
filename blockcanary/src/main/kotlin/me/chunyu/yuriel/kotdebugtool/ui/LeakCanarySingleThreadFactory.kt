package me.chunyu.yuriel.kotdebugtool.ui

import java.util.concurrent.ThreadFactory

/**
 * Created by yuriel on 8/9/16.
 */
internal class LeakCanarySingleThreadFactory(threadName: String) : ThreadFactory {

    private val threadName: String

    init {
        this.threadName = "LeakCanary-" + threadName
    }

    override fun newThread(runnable: Runnable): Thread {
        return Thread(runnable, threadName)
    }
}
