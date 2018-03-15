package com.exyui.android.debugbottle.components.fragments

import android.app.Activity
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import com.exyui.android.debugbottle.components.DTSettings
import com.exyui.android.debugbottle.components.bubbles.services.__DTBubble

/**
 * Created by yuriel on 9/3/16.
 */
open class __ContentFragment: Fragment() {
    open val TAG = "__ContentFragment"

    open protected var context: Activity? = null

    open val isHome = false

    // Use to update ui by bubble's status changes
    private val bubbleStatusChangeReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                this@__ContentFragment.onReceiveBubbleIntent(context, intent)
            }
        }
    }

    open fun onBackPressed(): Boolean = false

    open fun onReceiveBubbleIntent(context: Context, intent: Intent?) {
        throw NotImplementedError("onReceive must be implemented.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @Suppress("DEPRECATION", "OverridingDeprecatedMember")
    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        context = activity
    }

    protected fun registerBubbleStatusChangeReceiver() {
        val filter = IntentFilter()
        filter.addAction(__DTBubble.INTENT_ACTION)
        activity?.registerReceiver(bubbleStatusChangeReceiver, filter)
    }

    protected fun unregisterBubbleStatusChangeReceiver() {
        activity?.unregisterReceiver(bubbleStatusChangeReceiver)
    }

    fun selectItemAtDrawer(@StringRes res: Int) {
        (activity as DrawerActivity).selectItemByRes(res)
    }

    interface DrawerActivity {
        fun selectItemByRes(@StringRes res: Int)
    }
}