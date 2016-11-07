package com.exyui.android.debugbottle.components.testing

import android.app.Activity
import android.content.Context

/**
 * Created by yuriel on 11/7/16.
 *
 * The rule class of Monkey test black list
 */
internal object MonkeyExcludeActivities {

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
            activity.finish()
        }
    }
}