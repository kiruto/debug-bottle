package com.exyui.android.debugbottle.ui.layout

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.HONEYCOMB
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.exyui.android.debugbottle.core.__OnBlockEventInterceptor
import com.exyui.android.debugbottle.ui.R

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
        val channelId = "default"
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationCompat.Builder(context, channelId)
        else {
            NotificationCompat.Builder(context)
        }
        builder.setSmallIcon(R.drawable.__block_canary_notification)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createChannel(channelId))
        }
        notificationManager.notify(0xDEAFBEEF.toInt(), builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String): NotificationChannel? {
        return NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT)
    }
}
