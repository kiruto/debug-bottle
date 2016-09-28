package com.exyui.android.debugbottle.components.bubbles.services

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.exyui.android.debugbottle.components.DTActivityManager
import com.exyui.android.debugbottle.components.DTInstaller
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.RunningFeatureMgr
import com.exyui.android.debugbottle.components.bubbles.__BubbleLayout
import com.exyui.android.debugbottle.components.bubbles.__BubblesManager
import com.exyui.android.debugbottle.components.dialog.__TestingPreStartDialog

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
        val activity = DTActivityManager.topActivity
        if (null == activity){
            val msg = DTInstaller.getString(R.string.__dt_warning_open_activity_first)
            Toast.makeText(DTInstaller.getApplication(), msg, Toast.LENGTH_LONG).show()
            return
        } else if (activity.javaClass.name.startsWith("com.exyui")) {
            // I don't need you test debug bottle.
            val msg = DTInstaller.getString(R.string.__dt_warning_joker)
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
            return
        } else {
            __TestingPreStartDialog().show(activity.fragmentManager)
        }
    }

    override fun onBubbleRemoved(bubble: __BubbleLayout) {
        RunningFeatureMgr.remove(RunningFeatureMgr.MONKEY_TEST_RUNNER)
        notify()
    }
}