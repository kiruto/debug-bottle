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
        return (LayoutInflater.from(context).inflate(R.layout.__bubble_test_runner, null) as __BubbleLayout).apply {
            setShouldStickToWall(true)
            setOnBubbleClickListener(this@__TestingRunnerBubble)
            setOnBubbleRemoveListener(this@__TestingRunnerBubble)
            RunningFeatureMgr.add(RunningFeatureMgr.STRESS_TEST_RUNNER)
        }
    }

    override fun isRunning() = RunningFeatureMgr.has(RunningFeatureMgr.STRESS_TEST_RUNNER)

    override fun onBubbleClick(bubble: __BubbleLayout) {
        DTActivityManager.topActivity.let { activity ->
            when {
                null == activity -> {
                    val msg = DTInstaller.getString(R.string.__dt_warning_open_activity_first)
                    Toast.makeText(DTInstaller.getApplication(), msg, Toast.LENGTH_LONG).show()
                    return
                }
                activity.javaClass.name.startsWith("com.exyui") -> {
                    // I don't need you test debug bottle.
                    val msg = DTInstaller.getString(R.string.__dt_warning_joker)
                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
                    return
                }
                else -> __TestingPreStartDialog().show(activity.fragmentManager)
            }
        }
    }

    override fun onBubbleRemoved(bubble: __BubbleLayout) {
        RunningFeatureMgr.remove(RunningFeatureMgr.STRESS_TEST_RUNNER)
        notify()
    }
}