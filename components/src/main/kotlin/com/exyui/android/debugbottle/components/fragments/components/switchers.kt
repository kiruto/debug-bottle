package com.exyui.android.debugbottle.components.fragments.components

import android.app.Activity
import android.content.Context
import android.support.annotation.IdRes
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.Toast
import com.exyui.android.debugbottle.components.DTSettings
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.bubbles.services.__3DViewBubble
import com.exyui.android.debugbottle.components.floating.frame.__FloatFrame
import com.exyui.android.debugbottle.components.isSystemAlertPermissionGranted
import com.exyui.android.debugbottle.components.requestingPermissionDrawOverOtherApps
import com.exyui.android.debugbottle.components.widgets.DTListItemSwitch
import com.exyui.android.debugbottle.ui.BlockCanary

/**
 * Created by yuriel on 10/12/16.
 */
internal fun View.networkSwitcher(@IdRes id: Int): DTListItemSwitch {
    return (findViewById(id) as DTListItemSwitch).apply {
        isChecked = DTSettings.networkSniff
        setOnCheckedChangeListener { _, isChecked ->
            DTSettings.networkSniff = isChecked
        }
    }
}

internal fun View.strictSwitcher(@IdRes id: Int): DTListItemSwitch {
    return (findViewById(id) as DTListItemSwitch).apply {
        isChecked = DTSettings.strictMode
        setOnCheckedChangeListener { _, isChecked ->
            DTSettings.strictMode = isChecked
            hintRestart(context)
        }
    }
}

internal fun View.view3DSwitcher(@IdRes id: Int): DTListItemSwitch {
    return (findViewById(id) as DTListItemSwitch).apply {
        val activity = context as Activity
        isChecked = __3DViewBubble.isRunning()
        setOnCheckedChangeListener { _, isChecked ->
            if (!activity.isSystemAlertPermissionGranted()){
                this.isChecked = false
                activity.requestingPermissionDrawOverOtherApps(null)
            } else {
                if (isChecked) {
                    val started = __3DViewBubble.create(activity.applicationContext)
                    if (!started) {
                        this.isChecked = false
                        activity.requestingPermissionDrawOverOtherApps(null)
                    }
                } else {
                    __3DViewBubble.destroy(activity.applicationContext)
                }
            }
        }
    }
}

internal fun View.leakCanarySwitcher(@IdRes id: Int): DTListItemSwitch {
    return (findViewById(id) as DTListItemSwitch).apply {
        isChecked = DTSettings.leakCanaryEnable
        setOnCheckedChangeListener { _, isChecked ->
            DTSettings.leakCanaryEnable = isChecked
            hintRestart(context)
        }
    }
}

internal fun View.blockCanarySwitcherCompat(@IdRes id: Int): SwitchCompat {
    return (findViewById(id) as SwitchCompat).apply {
        isChecked = DTSettings.blockCanaryEnable
        setOnCheckedChangeListener(::blockCanarySwitcherListener)
    }
}

internal fun View.blockCanarySwitcher(@IdRes id: Int): DTListItemSwitch {
    return (findViewById(id) as DTListItemSwitch).apply {
        isChecked = DTSettings.blockCanaryEnable
        setOnCheckedChangeListener(::blockCanarySwitcherListener)
    }
}

internal fun View.bottleSwitch(@IdRes id: Int): DTListItemSwitch {
    return (findViewById(id) as DTListItemSwitch).apply {
        isChecked = DTSettings.bottleEnable
        setOnCheckedChangeListener { _, isChecked ->
            DTSettings.bottleEnable = isChecked
            hintRestart(context)
        }
    }
}

internal fun View.frameSwitcher(@IdRes id: Int): DTListItemSwitch {
    return (findViewById(id) as DTListItemSwitch).apply {
        val activity = context as Activity
        isChecked = DTSettings.frameEnable
        setOnCheckedChangeListener { _, isChecked ->
            DTSettings.frameEnable = isChecked
            if (isChecked) {
                __FloatFrame.start(activity)
            } else {
                __FloatFrame.stop(activity)
            }
        }
    }
}

internal fun View.monkeyBlackListSwitcher(@IdRes id: Int): DTListItemSwitch {
    return (findViewById(id) as DTListItemSwitch).apply {
        isChecked = DTSettings.monkeyBlacklist
        setOnCheckedChangeListener { _, isChecked ->
            DTSettings.monkeyBlacklist = isChecked
        }
    }
}

internal fun View.notificationLockSwitcher(@IdRes id: Int): DTListItemSwitch {
    return (findViewById(id) as DTListItemSwitch).apply {
        isChecked = DTSettings.notificationLock
        setOnCheckedChangeListener { _, isChecked ->
            DTSettings.notificationLock = isChecked
        }
    }
}

private fun hintRestart(context: Context) {
    Toast.makeText(context, R.string.__dt_need_restart_after_apply, Toast.LENGTH_LONG).show()
}

@Suppress("UNUSED_PARAMETER")
private fun blockCanarySwitcherListener(v: View, isChecked: Boolean) {
    DTSettings.blockCanaryEnable = isChecked
    try {
        if (isChecked) {
            BlockCanary.get().start()
        } else {
            BlockCanary.get().stop()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}