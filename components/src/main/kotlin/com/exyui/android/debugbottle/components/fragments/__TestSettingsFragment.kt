package com.exyui.android.debugbottle.components.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.RunningFeatureMgr
import com.exyui.android.debugbottle.components.bubbles.services.__DTBubble
import com.exyui.android.debugbottle.components.bubbles.services.__TestingRunnerBubble
import com.exyui.android.debugbottle.components.testing.MonkeyExcludeActivities
import com.exyui.android.debugbottle.components.widgets.DTListItemSwitch

/**
 * Created by yuriel on 9/20/16.
 */
class __TestSettingsFragment: __ContentFragment() {
    override val TAG = __TestSettingsFragment.TAG

    private lateinit var rootView: View

    private val testCtrlView: DTListItemSwitch by lazy {
        val result = rootView.findViewById(R.id.__dt_testing_enable) as DTListItemSwitch
        result.isChecked = RunningFeatureMgr.has(RunningFeatureMgr.STRESS_TEST_RUNNER)
        result.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                __TestingRunnerBubble.create(activity)
            } else {
                __TestingRunnerBubble.destroy(activity)
            }
        }
        result
    }

    private val showBlacklistView: ViewGroup by lazy {
        val result = rootView.findViewById(R.id.__dt_show_list) as ViewGroup
        result.setOnClickListener {
            MonkeyBlackListDialog().show(childFragmentManager, TAG)
        }
        result
    }

    companion object {
        val TAG = "__TestSettingsFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.__fragment_test_settings, container, false)
        testCtrlView; showBlacklistView
        registerBubbleStatusChangeReceiver()
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterBubbleStatusChangeReceiver()
    }

    override fun onReceiveBubbleIntent(context: Context, intent: Intent?) {
        when(intent?.extras?.getString(__DTBubble.KEY_TAG)) {
            __TestingRunnerBubble.TAG -> {
                val bubble3DStatus = intent?.extras?.getBoolean(__DTBubble.KEY_IS_RUNNING)?: false
                testCtrlView.isChecked = bubble3DStatus
            }
        }
    }

    class MonkeyBlackListDialog: DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1)

            for (name in MonkeyExcludeActivities.list.blackList) {
                adapter.add(name)
            }

            val builder = AlertDialog.Builder(context)
            builder.setAdapter(adapter) { dialog, witch ->

            }
            builder.setNegativeButton(R.string.__dt_close) { dialog, witch ->

            }
            return builder.create()
        }
    }
}