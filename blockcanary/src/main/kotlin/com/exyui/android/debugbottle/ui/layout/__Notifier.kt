package com.exyui.android.debugbottle.ui.layout

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.exyui.android.debugbottle.core.__OnBlockEventInterceptor
import com.exyui.android.debugbottle.ui.R

import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.HONEYCOMB
import android.os.Build.VERSION_CODES.JELLY_BEAN
/**
 * Created by yuriel on 8/9/16.
 */
internal class __Notifier : __OnBlockEventInterceptor {

    override fun onBlockEvent(context: Context, timeStart: String) {
        val intent = Intent(context, __DisplayBlockActivity::class.java)
        intent.putExtra("show_latest", timeStart)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(context, 1, intent, FLAG_UPDATE_CURRENT)
        val contentTitle = context.getString(R.string.__block_canary_class_has_blocked, timeStart)
        val contentText = context.getString(R.string.__block_canary_notification_message)
        show(context, contentTitle, contentText, pendingIntent)
    }

    @Suppress("DEPRECATION")
    @TargetApi(HONEYCOMB)
    private fun show(context: Context, contentTitle: String, contentText: String, pendingIntent: PendingIntent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification: Notification
//        if (SDK_INT < HONEYCOMB) {
//            notification = Notification()
//            notification.icon = R.drawable.__block_canary_notification
//            notification.`when` = System.currentTimeMillis()
//            notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
//            notification.defaults = Notification.DEFAULT_SOUND
//            notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent)
//        } else {
            val builder = Notification.Builder(context)
                    .setSmallIcon(R.drawable.__block_canary_notification)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND)
            if (SDK_INT < JELLY_BEAN) {
                notification = builder.notification
            } else {
                notification = builder.build()
            }
//        }
        notificationManager.notify(0xDEAFBEEF.toInt(), notification)
    }
}
