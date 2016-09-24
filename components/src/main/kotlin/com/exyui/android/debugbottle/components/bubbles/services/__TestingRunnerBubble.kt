package com.exyui.android.debugbottle.components.bubbles.services

import android.content.Context
import android.view.LayoutInflater
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.RunningFeatureMgr
import com.exyui.android.debugbottle.components.bubbles.__BubbleLayout
import com.exyui.android.debugbottle.components.bubbles.__BubblesManager

/**
 * Created by yuriel on 9/24/16.
 */
internal object __TestingRunnerBubble:
        __DTBubble(__BubblesManager),
        __BubbleLayout.OnBubbleClickListener,
        __BubbleLayout.OnBubbleRemoveListener {
    override val TAG: String = "__TestingRunnerBubble"

    override fun onCreateView(context: Context): __BubbleLayout {
        val bubbleView = LayoutInflater.from(context).inflate(R.layout.__bubble_test_runner, null) as __BubbleLayout
        bubbleView.setShouldStickToWall(true)
        bubbleView.setOnBubbleClickListener(this)
        bubbleView.setOnBubbleRemoveListener(this)
        RunningFeatureMgr.add(RunningFeatureMgr.MONKEY_TEST_RUNNER)
        return bubbleView
    }

    override fun isRunning() = RunningFeatureMgr.has(RunningFeatureMgr.MONKEY_TEST_RUNNER)

    override fun onBubbleClick(bubble: __BubbleLayout) {

    }

    override fun onBubbleRemoved(bubble: __BubbleLayout) {
        RunningFeatureMgr.remove(RunningFeatureMgr.MONKEY_TEST_RUNNER)
        notify()
    }
}