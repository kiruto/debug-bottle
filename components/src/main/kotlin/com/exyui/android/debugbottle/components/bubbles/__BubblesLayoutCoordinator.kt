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
        trashView?.let {
            it.visibility = View.VISIBLE
            if (checkIfBubbleIsOverTrash(bubble)) {
                it.applyMagnetism()
                //it.vibrate()
                applyTrashMagnetismToBubble(bubble)
            } else {
                it.releaseMagnetism()
            }
        }
    }

    private fun applyTrashMagnetismToBubble(bubble: __BubbleLayout) {
        getTrashContent()?.let {
            val trashCenterX = it.left + it.measuredWidth / 2
            val trashCenterY = it.top + it.measuredHeight / 2
            val x = trashCenterX - bubble.measuredWidth / 2
            val y = trashCenterY - bubble.measuredHeight / 2
            bubble.getViewParams().x = x
            bubble.getViewParams().y = y
            windowManager!!.updateViewLayout(bubble, bubble.getViewParams())
        }
    }

    private fun checkIfBubbleIsOverTrash(bubble: __BubbleLayout): Boolean {
        return if (trashView?.visibility == View.VISIBLE) {
            getTrashContent()?.let {
                val trashWidth = it.measuredWidth
                val trashHeight = it.measuredHeight
                val trashLeft = it.left - trashWidth / 2
                val trashRight = it.left + trashWidth + trashWidth / 2
                val trashTop = it.top - trashHeight / 2
                val trashBottom = it.top + trashHeight + trashHeight / 2
                val bubbleWidth = bubble.measuredWidth
                val bubbleHeight = bubble.measuredHeight
                val bubbleLeft = bubble.getViewParams().x
                val bubbleRight = bubbleLeft + bubbleWidth
                val bubbleTop = bubble.getViewParams().y
                val bubbleBottom = bubbleTop + bubbleHeight
                if (bubbleLeft >= trashLeft && bubbleRight <= trashRight) {
                    bubbleTop >= trashTop && bubbleBottom <= trashBottom
                } else {
                    false
                }
            }?: false
        } else {
            false
        }
    }

    fun notifyBubbleRelease(bubble: __BubbleLayout) {
        if (trashView != null) {
            if (checkIfBubbleIsOverTrash(bubble)) {
                bubblesService?.removeBubble(bubble)
            }
            trashView?.visibility = View.GONE
        }
    }

    class Builder(service: __BubblesService) {
        private val layoutCoordinator: __BubblesLayoutCoordinator = __BubblesLayoutCoordinator

        init {
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

        fun build(): __BubblesLayoutCoordinator = layoutCoordinator
    }

    private fun getTrashContent(): View? = trashView?.getChildAt(0)
}