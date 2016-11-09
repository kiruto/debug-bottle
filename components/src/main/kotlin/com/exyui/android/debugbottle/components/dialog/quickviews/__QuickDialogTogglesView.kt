package com.exyui.android.debugbottle.components.dialog.quickviews

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.AttributeSet
import android.widget.ScrollView
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.bubbles.services.__3DViewBubble
import com.exyui.android.debugbottle.components.bubbles.services.__DTBubble
import com.exyui.android.debugbottle.components.fragments.components.*

/**
 * Created by yuriel on 10/13/16.
 */
class __QuickDialogTogglesView: ScrollView {

    private val networkSwitcher by lazy { rootView?.networkSwitcher(R.id.__dt_network_switcher) }
    private val strictSwitcher by lazy { rootView?.strictSwitcher(R.id.__dt_strict_switcher) }
    private val view3DSwitcher by lazy { rootView?.view3DSwitcher(R.id.__dt_3d_switcher) }
    private val leakCanarySwitcher by lazy { rootView?.leakCanarySwitcher(R.id.__dt_leak_canary_switcher) }
    private val blockCanarySwitcher by lazy { rootView?.blockCanarySwitcher(R.id.__dt_block_canary_switcher) }
    private val frameSwitcher by lazy { rootView?.frameSwitcher(R.id.__dt_frame_switcher) }
    private val monkeyBlackListSwitcher by lazy { rootView?.monkeyBlackListSwitcher(R.id.__dt_monkey_black_list) }
    private val notificationLockSwitcher by lazy { rootView?.notificationLockSwitcher(R.id.__dt_notification_lock) }

    constructor(context: Context): super(context) {
        init()
    }
    constructor(context: Context, attr: AttributeSet): super(context, attr) {
        init()
    }
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int): super(context, attr, defStyleAttr) {
        init()
    }

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("NewApi")
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attr, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        bindViews()
    }

    private fun bindViews() {
        inflate(context, R.layout.__fragment_quick_toggles, this)
        networkSwitcher; strictSwitcher; view3DSwitcher; leakCanarySwitcher; blockCanarySwitcher
        frameSwitcher; monkeyBlackListSwitcher; notificationLockSwitcher
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        registerBubbleStatusChangeReceiver()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregisterBubbleStatusChangeReceiver()
    }

    // Use to update ui by bubble's status changes
    private val bubbleStatusChangeReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                this@__QuickDialogTogglesView.onReceiveBubbleIntent(context, intent)
            }
        }
    }

    private var receiverRegistered = false

    private fun registerBubbleStatusChangeReceiver() {
        val filter = IntentFilter()
        filter.addAction(__DTBubble.INTENT_ACTION)
        context.registerReceiver(bubbleStatusChangeReceiver, filter)
        receiverRegistered = true
    }

    private fun unregisterBubbleStatusChangeReceiver() {
        if (receiverRegistered) {
            context.unregisterReceiver(bubbleStatusChangeReceiver)
        }
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