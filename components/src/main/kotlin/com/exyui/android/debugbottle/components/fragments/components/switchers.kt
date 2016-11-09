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
    val result = findViewById(id) as DTListItemSwitch
    result.isChecked = DTSettings.networkSniff
    result.setOnCheckedChangeListener { view, isChecked ->
        DTSettings.networkSniff = isChecked
    }
    return result
}

internal fun View.strictSwitcher(@IdRes id: Int): DTListItemSwitch {
    val result = findViewById(id) as DTListItemSwitch
    result.isChecked = DTSettings.strictMode
    result.setOnCheckedChangeListener { view, isChecked ->
        DTSettings.strictMode = isChecked
        hintRestart(context)
    }
    return result
}

internal fun View.view3DSwitcher(@IdRes id: Int): DTListItemSwitch {
    val result = findViewById(id) as DTListItemSwitch
    val activity = context as Activity
    result.isChecked = __3DViewBubble.isRunning()
    result.setOnCheckedChangeListener { view, isChecked ->
        if (!activity.isSystemAlertPermissionGranted()){
            result.isChecked = false
            activity.requestingPermissionDrawOverOtherApps(null)
        } else {
            if (isChecked) {
                val started = __3DViewBubble.create(activity.applicationContext)
                if (!started) {
                    result.isChecked = false
                    activity.requestingPermissionDrawOverOtherApps(null)
                }
            } else {
                __3DViewBubble.destroy(activity.applicationContext)
            }
        }
    }
    return result
}

internal fun View.leakCanarySwitcher(@IdRes id: Int): DTListItemSwitch {
    val result = findViewById(id) as DTListItemSwitch
    result.isChecked = DTSettings.leakCanaryEnable
    result.setOnCheckedChangeListener { view, isChecked ->
        DTSettings.leakCanaryEnable = isChecked
        hintRestart(context)
    }
    return result
}

internal fun View.blockCanarySwitcherCompat(@IdRes id: Int): SwitchCompat {
    val result = findViewById(id) as SwitchCompat
    result.isChecked = DTSettings.blockCanaryEnable
    result.setOnCheckedChangeListener(::blockCanarySwitcherListener)
    return result
}

internal fun View.blockCanarySwitcher(@IdRes id: Int): DTListItemSwitch {
    val result = findViewById(id) as DTListItemSwitch
    result.isChecked = DTSettings.blockCanaryEnable
    result.setOnCheckedChangeListener(::blockCanarySwitcherListener)
    return result
}

internal fun View.bottleSwitch(@IdRes id: Int): DTListItemSwitch {
    val result = findViewById(id) as DTListItemSwitch
    result.isChecked = DTSettings.bottleEnable
    result.setOnCheckedChangeListener { view, isChecked ->
        DTSettings.bottleEnable = isChecked
        hintRestart(context)
    }
    return result
}

internal fun View.frameSwitcher(@IdRes id: Int): DTListItemSwitch {
    val result = findViewById(id) as DTListItemSwitch
    val activity = context as Activity
    result.isChecked = DTSettings.frameEnable
    result.setOnCheckedChangeListener { view, isChecked ->
        DTSettings.frameEnable = isChecked
        if (isChecked) {
            __FloatFrame.start(activity)
        } else {
            __FloatFrame.stop(activity)
        }
    }
    return result
}

internal fun View.monkeyBlackListSwitcher(@IdRes id: Int): DTListItemSwitch {
    val result = findViewById(id) as DTListItemSwitch
    result.isChecked = DTSettings.monkeyBlacklist
    result.setOnCheckedChangeListener { view, isChecked ->
        if (isChecked) {
            DTSettings.monkeyBlacklist = true
            result.enable = false
            Toast.makeText(context, R.string.__dt_monkey_black_list_enabled, Toast.LENGTH_LONG).show()
        }
    }
    return result
}

internal fun View.notificationLockSwitcher(@IdRes id: Int): DTListItemSwitch {
    val result = findViewById(id) as DTListItemSwitch
    result.isChecked = DTSettings.notificationLock
    result.setOnCheckedChangeListener { view, isChecked ->
        DTSettings.notificationLock = isChecked
    }
    return result
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