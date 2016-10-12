package com.exyui.android.debugbottle.core

/**
 * Created by yuriel on 9/30/16.
 */
object __FrameRecorder {
    var enabled = false

    var average: Double = .0
        private set

    private val POOL_SIZE = 30

    private var counter = 0
    private var temp = 0L
    private var sum = 0L
    var listener: ((Double) -> Unit)? = null

    fun record(timestamp: Long) {

        if (!enabled) return

        if (temp == 0L) {
            temp = timestamp
        } else {
            temp = timestamp - temp
            put(temp)
            temp = 0
        }
    }

    private fun put (temp: Long) {
        if (counter < POOL_SIZE) {
            sum += temp
        } else {
            compute()
            counter = 0
            sum = 0
        }
        counter ++
    }

    private fun compute() {
        average = sum.toDouble() / POOL_SIZE.toDouble()
        listener?.invoke(average)
    }
}