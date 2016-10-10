package com.exyui.android.debugbottle.components.displayperformance

import android.annotation.TargetApi
import android.os.Build
import android.view.Choreographer
import java.util.concurrent.TimeUnit

/**
 * Created by yuriel on 10/10/16.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
internal class __DTHeartBeat : Choreographer.FrameCallback {

    private val choreographer: Choreographer

    private var frameStartTime: Long = 0
    private var framesRendered = 0

    private val listeners = mutableListOf<(fps: Double) -> Unit>()
    private var interval = 500

    init {
        choreographer = Choreographer.getInstance()
    }

    fun start() {
        choreographer.postFrameCallback(this)
    }

    fun stop() {
        frameStartTime = 0
        framesRendered = 0
        choreographer.removeFrameCallback(this)
    }

    fun addListener(l: (fps: Double) -> Unit) {
        listeners.add(l)
    }

    fun setInterval(interval: Int) {
        this.interval = interval
    }

    override fun doFrame(frameTimeNanos: Long) {
        val currentTimeMillis = TimeUnit.NANOSECONDS.toMillis(frameTimeNanos)

        if (frameStartTime > 0) {
            // take the span in milliseconds
            val timeSpan = currentTimeMillis - frameStartTime
            framesRendered++

            if (timeSpan > interval) {
                val fps = framesRendered * 1000 / timeSpan.toDouble()

                frameStartTime = currentTimeMillis
                framesRendered = 0

                for (audience in listeners) {
                    audience(fps)
                }
            }
        } else {
            frameStartTime = currentTimeMillis
        }

        choreographer.postFrameCallback(this)
    }
}