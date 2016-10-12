package com.exyui.android.debugbottle.components.dialog

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import com.exyui.android.debugbottle.components.DTActivityManager
import com.exyui.android.debugbottle.components.DTDrawerActivity
import com.exyui.android.debugbottle.components.DTInstaller
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.bubbles.services.__3DViewBubble
import com.exyui.android.debugbottle.components.bubbles.services.__DTBubble
import com.exyui.android.debugbottle.components.fragments.components.*
import com.exyui.android.debugbottle.components.widgets.__FloatAnimatedDialog
import com.exyui.android.debugbottle.components.widgets.__FloatingDialogHeaderLayout

/**
 * Created by yuriel on 10/12/16.
 */
class __TogglesDialog: __FloatAnimatedDialog() {

    companion object {
        val TAG = "__TogglesDialog"
    }
    override val TAG = __TogglesDialog.TAG
    override val title = R.string.__dt_toggles

    private var rootView: View? = null

    private val networkSwitcher by lazy { rootView?.networkSwitcher(R.id.__dt_network_switcher) }
    private val strictSwitcher by lazy { rootView?.strictSwitcher(R.id.__dt_strict_switcher) }
    private val view3DSwitcher by lazy { rootView?.view3DSwitcher(R.id.__dt_3d_switcher) }
    private val leakCanarySwitcher by lazy { rootView?.leakCanarySwitcher(R.id.__dt_leak_canary_switcher) }
    private val blockCanarySwitcher by lazy { rootView?.blockCanarySwitcher(R.id.__dt_block_canary_switcher) }
    private val frameSwitcher by lazy { rootView?.frameSwitcher(R.id.__dt_frame_switcher) }

    override fun createView(): View {
        val result = activity.layoutInflater.inflate(R.layout.__dialog_toggles, null)

        val header = result.findViewById(R.id.__floating_header) as __FloatingDialogHeaderLayout
        header.setAction { startDTDrawerActivity(true) }
        header.setClose { dismiss() }
        result.findViewById(R.id.__dt_run_test).setOnClickListener { startDTDrawerActivity() }
        rootView = result
        bindViews()
        isCancelable = false

        registerBubbleStatusChangeReceiver()
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterBubbleStatusChangeReceiver()
    }

    private fun bindViews() {
        networkSwitcher; strictSwitcher; view3DSwitcher; leakCanarySwitcher; blockCanarySwitcher
        frameSwitcher
    }

    private fun startDTDrawerActivity(openSettings: Boolean = false) {
        if (DTActivityManager.topActivity?.javaClass == DTDrawerActivity::class.java) {
            dismiss()
        }
        val intent = Intent(DTInstaller.app, DTDrawerActivity::class.java)
        if (openSettings) {
            intent.putExtra(DTDrawerActivity.KEY_SELECTED, R.string.__dt_settings)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        DTInstaller.app?.startActivity(intent)
    }

    // Use to update ui by bubble's status changes
    private val bubbleStatusChangeReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                this@__TogglesDialog.onReceiveBubbleIntent(context, intent)
            }
        }
    }

    private fun registerBubbleStatusChangeReceiver() {
        val filter = IntentFilter()
        filter.addAction(__DTBubble.INTENT_ACTION)
        activity.registerReceiver(bubbleStatusChangeReceiver, filter)
    }

    private fun unregisterBubbleStatusChangeReceiver() {
        activity.unregisterReceiver(bubbleStatusChangeReceiver)
    }

    private fun onReceiveBubbleIntent(context: Context, intent: Intent?) {
        when(intent?.extras?.getString(__DTBubble.KEY_TAG)) {
            __3DViewBubble.TAG -> {
                val bubble3DStatus = intent?.extras?.getBoolean(__DTBubble.KEY_IS_RUNNING)?: false
                view3DSwitcher?.isChecked = bubble3DStatus
            }
        }
    }
}