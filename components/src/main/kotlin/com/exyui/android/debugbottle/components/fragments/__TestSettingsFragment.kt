package com.exyui.android.debugbottle.components.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.RunningFeatureMgr
import com.exyui.android.debugbottle.components.bubbles.services.__TestingRunnerBubble
import com.exyui.android.debugbottle.components.widgets.DTListItemSwitch

/**
 * Created by yuriel on 9/20/16.
 */
class __TestSettingsFragment: __ContentFragment() {
    override val TAG = __TestSettingsFragment.TAG

    private lateinit var rootView: View

    private val testCtrlView: DTListItemSwitch by lazy {
        val result = rootView.findViewById(R.id.__dt_testing_enable) as DTListItemSwitch
        result.isChecked = RunningFeatureMgr.has(RunningFeatureMgr.MONKEY_TEST_RUNNER)
        result.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                __TestingRunnerBubble.create(activity)
            } else {
                __TestingRunnerBubble.destroy(activity)
            }
        }
        result
    }

    companion object {
        val TAG = "__TestSettingsFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.__fragment_test_settings, container, false)
        testCtrlView
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}