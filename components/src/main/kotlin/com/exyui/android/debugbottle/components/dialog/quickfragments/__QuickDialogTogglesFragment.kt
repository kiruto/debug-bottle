package com.exyui.android.debugbottle.components.dialog.quickfragments

import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.bubbles.services.__3DViewBubble
import com.exyui.android.debugbottle.components.bubbles.services.__DTBubble
import com.exyui.android.debugbottle.components.fragments.components.*

/**
 * Created by yuriel on 10/13/16.
 */
class __QuickDialogTogglesFragment: __BaseQuickDialogFragment() {

    companion object {
        val TAG = "__QuickDialogTogglesFragment"
    }

    val TAG = __QuickDialogTogglesFragment.TAG

    private var rootView: View? = null

    private val networkSwitcher by lazy { rootView?.networkSwitcher(R.id.__dt_network_switcher) }
    private val strictSwitcher by lazy { rootView?.strictSwitcher(R.id.__dt_strict_switcher) }
    private val view3DSwitcher by lazy { rootView?.view3DSwitcher(R.id.__dt_3d_switcher) }
    private val leakCanarySwitcher by lazy { rootView?.leakCanarySwitcher(R.id.__dt_leak_canary_switcher) }
    private val blockCanarySwitcher by lazy { rootView?.blockCanarySwitcher(R.id.__dt_block_canary_switcher) }
    private val frameSwitcher by lazy { rootView?.frameSwitcher(R.id.__dt_frame_switcher) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.__fragment_quick_toggles, container, false)
        bindViews()

        registerBubbleStatusChangeReceiver()
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterBubbleStatusChangeReceiver()
    }

    private fun bindViews() {
        networkSwitcher; strictSwitcher; view3DSwitcher; leakCanarySwitcher; blockCanarySwitcher
        frameSwitcher
    }

    // Use to update ui by bubble's status changes
    private val bubbleStatusChangeReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                this@__QuickDialogTogglesFragment.onReceiveBubbleIntent(context, intent)
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

    @Suppress("UNUSED_PARAMETER")
    private fun onReceiveBubbleIntent(context: Context, intent: Intent?) {
        when(intent?.extras?.getString(__DTBubble.KEY_TAG)) {
            __3DViewBubble.TAG -> {
                val bubble3DStatus = intent?.extras?.getBoolean(__DTBubble.KEY_IS_RUNNING)?: false
                view3DSwitcher?.isChecked = bubble3DStatus
            }
        }
    }
}