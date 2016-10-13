package com.exyui.android.debugbottle.components.dialog.quickviews

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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

    constructor(context: Context): super(context) {
        init(context)
    }
    constructor(context: Context, attr: AttributeSet): super(context, attr) {
        init(context, attr)
    }
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int): super(context, attr, defStyleAttr) {
        init(context, attr, defStyleAttr)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("NewApi")
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attr, defStyleAttr, defStyleRes) {
        init(context, attr, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attr: AttributeSet? = null, defStyleAttr: Int? = null, defStyleRes: Int? = null) {
        bindViews()
        initAttrs(context, attr, defStyleAttr?: 0, defStyleRes?: 0)
    }

    private fun bindViews() {
        inflate(context, R.layout.__fragment_quick_toggles, this)
        networkSwitcher; strictSwitcher; view3DSwitcher; leakCanarySwitcher; blockCanarySwitcher
        frameSwitcher
    }

    private fun initAttrs(context: Context, attr: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
//        val ta = context.theme.obtainStyledAttributes(attr, R.styleable.__DTListItem, defStyleAttr, defStyleRes)
//        try {
//            title = ta.getString(R.styleable.__DTListItem___dt_title)?: ""
//            content = ta.getString(R.styleable.__DTListItem___dt_content)?: ""
//            isSmart = ta.getBoolean(R.styleable.__DTListItem___dt_smart, false)
//        } finally {
//            ta.recycle()
//        }
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

    private fun registerBubbleStatusChangeReceiver() {
        val filter = IntentFilter()
        filter.addAction(__DTBubble.INTENT_ACTION)
        context.registerReceiver(bubbleStatusChangeReceiver, filter)
    }

    private fun unregisterBubbleStatusChangeReceiver() {
        context.unregisterReceiver(bubbleStatusChangeReceiver)
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