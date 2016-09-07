package com.exyui.android.debugbottle.components.injector

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import com.exyui.android.debugbottle.components.DebugWidgets
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by yuriel on 8/15/16.
 */
open class __IntentInjectorImpl : Injectable<Intent> {

    override val model: LinkedHashMap<String, Intent> = LinkedHashMap()

    @Volatile
    private var activityWrapper: WeakReference<Activity>? = null

    fun setActivity(activity: Activity) {
        activityWrapper = WeakReference(activity)
    }

    override fun put(name: String, t: Intent) {
        model.put(name, t)
    }

    override fun get(name: String): Intent? = model[name]

    override fun run(t: Intent?) {
        val activity = activityWrapper?.get()
        if (null == activity) return else
            try {
                DebugWidgets.runActivityWithIntent(activity, t?: return)
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    override fun run(name: String) {
        run(this[name])
    }
}