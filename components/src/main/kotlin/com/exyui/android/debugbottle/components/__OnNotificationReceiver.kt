package com.exyui.android.debugbottle.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.exyui.android.debugbottle.components.dialog.__TogglesDialog

/**
 * Created by yuriel on 10/12/16.
 */
internal class __OnNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (null == DTActivityManager.topActivity) {
            val i = Intent(context?.applicationContext, DTDrawerActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra(DTDrawerActivity.KEY_SHOW_DRAWER, false)
            context?.applicationContext?.startActivity(i)
        } else {
            __TogglesDialog().show(DTActivityManager.topActivity!!.fragmentManager)
        }
    }
}