@file:Suppress("unused")

package com.exyui.android.debugbottle.components.injector

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.exyui.android.debugbottle.components.DTActivityManager
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.testing.MonkeyExcludeActivities

/**
 * Created by yuriel on 8/15/16.
 */
abstract class Injector {
    private val intentInjector = IntentInjector
    private val runnableInjector = RunnableInjector
    private val quickEntry = QuickEntry
    private val blackList = MonkeyExcludeActivities.list

    protected val activity: Activity?
        get() = DTActivityManager.topActivity

    protected fun put(intent: Intent, name: String = intent.toString()) {
        intentInjector.put(name, intent)
    }

    protected fun put(runnable: Runnable, name: String = runnable.toString()) {
        runnableInjector.put(name, runnable)
    }

    protected fun quickEntry(at: Class<out Activity>, name: String, t: Runnable) {
        quickEntry.put(at, name, t)
    }

    protected fun quickEntry(name: String, listener: QuickEntry.OnActivityDisplayedListener) {
        quickEntry.put(name, listener)
    }

    protected fun excludeFromMonkey(activityClass: Class<out Activity>) {
        blackList.exclude(activityClass)
    }

    protected fun excludeFromMonkey(activityClassName: String) {
        blackList.exclude(activityClassName)
    }

    abstract fun inject()

    open fun beforeMonkeyTest() = Toast.makeText(activity, R.string.__dt_monkey_environment_ready, Toast.LENGTH_SHORT).show()
}