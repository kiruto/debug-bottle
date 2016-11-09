package com.exyui.android.debugbottle.components.testing

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.exyui.android.debugbottle.components.R

/**
 * Created by yuriel on 11/7/16.
 *
 * The rule class of Monkey test black list
 */
internal object MonkeyExcludeActivities {

    private val TAG = "MonkeyExcludeActivities"

    lateinit var list: MonkeyBlackList
        private set

    fun init(context: Context) {
        list = MonkeyBlackList.getInstance(context)

                // Debug Bottle
                .exclude("com.exyui.*")

                // Leak Canary
                .exclude("com.squareup.leakcanary.internal.DisplayLeakActivity")
                .exclude("com.squareup.leakcanary.internal.RequestStoragePermissionActivity")
    }

    fun activityStarted(activity: Activity) {
        try { list } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
            return
        }
        if (list.contains(activity.javaClass)) {
            Toast.makeText(activity, R.string.__dt_monkey_blacklist_activity_closed, Toast.LENGTH_SHORT).show()
            activity.finish()
            Log.d(TAG, "Activity ${activity.javaClass} is closed")
        }
    }
}