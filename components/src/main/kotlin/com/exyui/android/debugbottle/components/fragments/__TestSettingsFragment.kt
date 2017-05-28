package com.exyui.android.debugbottle.components.fragments

import android.app.Activity
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
        (rootView.findViewById(R.id.__dt_testing_enable) as DTListItemSwitch).apply {
            isChecked = RunningFeatureMgr.has(RunningFeatureMgr.STRESS_TEST_RUNNER)
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    __TestingRunnerBubble.create(activity)
                } else {
                    __TestingRunnerBubble.destroy(activity)
                }
            }
        }
    }

    private val showBlacklistView: ViewGroup by lazy {
        (rootView.findViewById(R.id.__dt_show_list) as ViewGroup).apply {
            setOnClickListener {
                MonkeyBlackListDialog().show(childFragmentManager, TAG)
            }
        }
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
        private lateinit var activityContext: Context

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val adapter = ArrayAdapter<String>(activityContext, android.R.layout.simple_list_item_1, android.R.id.text1)

            adapter.addAll(MonkeyExcludeActivities.list.blackList)

            val builder = AlertDialog.Builder(activityContext)
            builder.setAdapter(adapter) { dialog, witch ->

            }
            builder.setNegativeButton(R.string.__dt_close) { dialog, witch ->

            }
            return builder.create()
        }

        @Suppress("OverridingDeprecatedMember")
        override fun onAttach(context: Activity?) {
            @Suppress("DEPRECATION")
            super.onAttach(context)
            context?.let { activityContext = it }
        }
    }
}