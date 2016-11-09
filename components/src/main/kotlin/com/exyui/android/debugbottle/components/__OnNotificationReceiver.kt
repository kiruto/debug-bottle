package com.exyui.android.debugbottle.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.exyui.android.debugbottle.components.dialog.__QuickDialog
import com.exyui.android.debugbottle.components.dialog.__UnlockDialog

/**
 * Created by yuriel on 10/12/16.
 * Main receiver. When Bottle notification has clicked, it will be called.
 */
internal class __OnNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (null == DTActivityManager.topActivity) {
            if (DTSettings.notificationLock) {
                Toast.makeText(DTInstaller.app, R.string.__dt_warning_open_activity_first, Toast.LENGTH_SHORT).show()
            } else {
                val i = Intent(context?.applicationContext, DTDrawerActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.putExtra(DTDrawerActivity.KEY_SHOW_DRAWER, false)
                context?.applicationContext?.startActivity(i)
            }
        } else {
            if (DTSettings.notificationLock) {
                __UnlockDialog().show(DTActivityManager.topActivity!!.fragmentManager)
            } else {
                __QuickDialog().show(DTActivityManager.topActivity!!.fragmentManager)
            }
        }
    }
}