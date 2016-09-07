package com.exyui.android.debugbottle.components

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent

/**
 * Created by yuriel on 8/16/16.
 */
internal object DebugWidgets {
    fun runActivityWithIntent(activity: Activity, intent: Intent) {
        val dialog = DialogsCollection.RunActivityDialogFragment.newInstance(intent)
        dialog.show(activity.fragmentManager, "dialog")
    }
}