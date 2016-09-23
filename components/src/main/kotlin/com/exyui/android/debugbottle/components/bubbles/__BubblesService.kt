package com.exyui.android.debugbottle.components.bubbles

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import java.util.*

/**
 * Created by yuriel on 9/22/16.
 */
internal class __BubblesService : Service() {
    private val binder = BubblesServiceBinder()
    private val bubbles = mutableListOf<__BubbleLayout>()
    private var bubblesTrash: __BubbleTrashLayout? = null
    private val cb = mutableListOf<__OnBubbleStatusChangeCallback>()
    private var windowManager: WindowManager? = null
        get() {
            if (field == null) {
                field = getSystemService(WINDOW_SERVICE) as WindowManager
            }
            return field
        }
    private var layoutCoordinator: __BubblesLayoutCoordinator? = null

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        for (bubble in bubbles) {
            recycleBubble(bubble)
        }
        bubbles.clear()
        return super.onUnbind(intent)
    }

    private fun recycleBubble(bubble: __BubbleLayout) {
        Handler(Looper.getMainLooper()).post {
            try {
                windowManager?.removeView(bubble)
                for (cachedBubble in bubbles) {
                    if (cachedBubble === bubble) {
                        bubble.notifyBubbleRemoved()
                        bubbles.remove(cachedBubble)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addBubble(bubble: __BubbleLayout, x: Int, y: Int) {
        val layoutParams = buildLayoutParamsForBubble(x, y)
        bubble.windowManager = windowManager!!
        bubble.setViewParams(layoutParams)
        bubble.setLayoutCoordinator(layoutCoordinator)
        bubbles.add(bubble)
        addViewToWindow(bubble)
        cb.forEach { it.onAdd() }
    }

    fun addTrash(trashLayoutResourceId: Int) {
        if (trashLayoutResourceId != 0) {
            bubblesTrash = __BubbleTrashLayout(this)
            bubblesTrash!!.windowManager = windowManager!!
            bubblesTrash!!.setViewParams(buildLayoutParamsForTrash())
            bubblesTrash!!.visibility = View.GONE
            LayoutInflater.from(this).inflate(trashLayoutResourceId, bubblesTrash, true)
            addViewToWindow(bubblesTrash!!)
            initializeLayoutCoordinator()
        }
    }

    fun addCallback(cb: __OnBubbleStatusChangeCallback) {
        this.cb.add(cb)
    }

    fun removeCallback(cb: __OnBubbleStatusChangeCallback) {
        this.cb.remove(cb)
    }

    private fun initializeLayoutCoordinator() {
        layoutCoordinator = __BubblesLayoutCoordinator.Builder(this)
                .setWindowManager(windowManager!!)
                .setTrashView(bubblesTrash!!)
                .build()
    }

    private fun addViewToWindow(view: __BubbleBaseLayout) {
        Handler(Looper.getMainLooper()).post {
            windowManager?.addView(view, view.getViewParams())
        }
    }

    private fun buildLayoutParamsForBubble(x: Int, y: Int): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT)
        params.gravity = Gravity.TOP or Gravity.START
        params.x = x
        params.y = y
        return params
    }

    private fun buildLayoutParamsForTrash(): WindowManager.LayoutParams {
        val x = 0
        val y = 0
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT)
        params.x = x
        params.y = y
        return params
    }

    fun removeBubble(bubble: __BubbleLayout) {
        recycleBubble(bubble)
        cb.forEach { it.onRemove() }
    }

    inner class BubblesServiceBinder : Binder() {
        val service: __BubblesService
            get() = this@__BubblesService
    }
}