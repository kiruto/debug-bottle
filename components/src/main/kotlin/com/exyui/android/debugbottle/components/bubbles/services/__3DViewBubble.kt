package com.exyui.android.debugbottle.components.bubbles.services

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.exyui.android.debugbottle.components.DTActivityManager
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.RunningFeatureMgr
import com.exyui.android.debugbottle.components.bubbles.__BubbleLayout
import com.exyui.android.debugbottle.components.bubbles.__BubblesManager
import com.exyui.android.debugbottle.components.widgets.__ScalpelFrameLayout

/**
 * Created by yuriel on 9/23/16.
 */
internal object __3DViewBubble:
        __DTBubble(__BubblesManager),
        __BubbleLayout.OnBubbleClickListener,
        __BubbleLayout.OnBubbleRemoveListener {

    override val TAG = "__3DViewBubble"

    override fun onCreateView(context: Context): __BubbleLayout {
        val bubbleView = LayoutInflater.from(context).inflate(R.layout.__bubble_3d, null) as __BubbleLayout
        bubbleView.setShouldStickToWall(true)
        bubbleView.setOnBubbleClickListener(this)
        bubbleView.setOnBubbleRemoveListener(this)
        RunningFeatureMgr.add(RunningFeatureMgr.VIEW_3D_WINDOW)
        return bubbleView
    }

    override fun onDestroy(context: Context) {
        super.onDestroy(context)
    }

    override fun isRunning() = RunningFeatureMgr.has(RunningFeatureMgr.VIEW_3D_WINDOW)

    override fun onBubbleRemoved(bubble: __BubbleLayout) {
        RunningFeatureMgr.remove(RunningFeatureMgr.VIEW_3D_WINDOW)
        notify()
    }

    override fun onBubbleClick(bubble: __BubbleLayout) {
        val activity = DTActivityManager.topActivity
        activity?: return
        attachActivityTo3DView(activity)
    }

    private fun attachActivityTo3DView(activity: Activity) {
        val layout: __ScalpelFrameLayout
        val decorView: ViewGroup = activity.window.decorView as ViewGroup
        if (decorView.findViewById(R.id.__dt_scalpel_frame_layout) == null) {
            layout = __ScalpelFrameLayout(activity)
            layout.id = R.id.__dt_scalpel_frame_layout
            val rootView = decorView.getChildAt(0)

            decorView.removeView(rootView)
            layout.addView(rootView)
            decorView.addView(layout)
        } else {
            layout = decorView.findViewById(R.id.__dt_scalpel_frame_layout) as __ScalpelFrameLayout
        }

        if (!layout.isLayerInteractionEnabled) {
            layout.isLayerInteractionEnabled = true
            layout.setDrawIds(true)
        } else {
            layout.isLayerInteractionEnabled = false
        }
    }
}