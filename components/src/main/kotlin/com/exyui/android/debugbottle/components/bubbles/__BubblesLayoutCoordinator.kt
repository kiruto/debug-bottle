package com.exyui.android.debugbottle.components.bubbles

import android.view.View
import android.view.WindowManager

/**
 * Created by yuriel on 9/22/16.
 */
internal object __BubblesLayoutCoordinator {
    private var trashView: __BubbleTrashLayout? = null
    private var windowManager: WindowManager? = null
    private var bubblesService: __BubblesService? = null

    fun notifyBubblePositionChanged(bubble: __BubbleLayout,
                                    @Suppress("UNUSED_PARAMETER") x: Int,
                                    @Suppress("UNUSED_PARAMETER") y: Int) {
        if (trashView != null) {
            trashView!!.visibility = View.VISIBLE
            if (checkIfBubbleIsOverTrash(bubble)) {
                trashView!!.applyMagnetism()
                trashView!!.vibrate()
                applyTrashMagnetismToBubble(bubble)
            } else {
                trashView!!.releaseMagnetism()
            }
        }
    }

    private fun applyTrashMagnetismToBubble(bubble: __BubbleLayout) {
        val trashContentView = getTrashContent()
        val trashCenterX = trashContentView.left + trashContentView.measuredWidth / 2
        val trashCenterY = trashContentView.top + trashContentView.measuredHeight / 2
        val x = trashCenterX - bubble.measuredWidth / 2
        val y = trashCenterY - bubble.measuredHeight / 2
        bubble.getViewParams().x = x
        bubble.getViewParams().y = y
        windowManager!!.updateViewLayout(bubble, bubble.getViewParams())
    }

    private fun checkIfBubbleIsOverTrash(bubble: __BubbleLayout): Boolean {
        var result = false
        if (trashView!!.visibility === View.VISIBLE) {
            val trashContentView = getTrashContent()
            val trashWidth = trashContentView.measuredWidth
            val trashHeight = trashContentView.measuredHeight
            val trashLeft = trashContentView.left - trashWidth / 2
            val trashRight = trashContentView.left + trashWidth + trashWidth / 2
            val trashTop = trashContentView.top - trashHeight / 2
            val trashBottom = trashContentView.top + trashHeight + trashHeight / 2
            val bubbleWidth = bubble.measuredWidth
            val bubbleHeight = bubble.measuredHeight
            val bubbleLeft = bubble.getViewParams().x
            val bubbleRight = bubbleLeft + bubbleWidth
            val bubbleTop = bubble.getViewParams().y
            val bubbleBottom = bubbleTop + bubbleHeight
            if (bubbleLeft >= trashLeft && bubbleRight <= trashRight) {
                if (bubbleTop >= trashTop && bubbleBottom <= trashBottom) {
                    result = true
                }
            }
        }
        return result
    }

    fun notifyBubbleRelease(bubble: __BubbleLayout) {
        if (trashView != null) {
            if (checkIfBubbleIsOverTrash(bubble)) {
                bubblesService!!.removeBubble(bubble)
            }
            trashView!!.visibility = View.GONE
        }
    }

    class Builder(service: __BubblesService) {
        private val layoutCoordinator: __BubblesLayoutCoordinator

        init {
            layoutCoordinator = __BubblesLayoutCoordinator
            layoutCoordinator.bubblesService = service
        }

        fun setTrashView(trashView: __BubbleTrashLayout): Builder {
            layoutCoordinator.trashView = trashView
            return this
        }

        fun setWindowManager(windowManager: WindowManager): Builder {
            layoutCoordinator.windowManager = windowManager
            return this
        }

        fun build(): __BubblesLayoutCoordinator {
            return layoutCoordinator
        }
    }

    private fun getTrashContent(): View {
        return trashView!!.getChildAt(0)
    }
}