package me.chunyu.yuriel.kotdebugtool.components.injector

import android.app.Activity
import android.content.Intent
import me.chunyu.yuriel.kotdebugtool.components.DTActivityManager

/**
 * Created by yuriel on 8/15/16.
 */
abstract class Injector {
    private val intentInjector = IntentInjector
    private val runnableInjector = RunnableInjector

    protected val activity: Activity?
        get() = DTActivityManager.topActivity

    protected fun put(intent: Intent, name: String = intent.toString()) {
        intentInjector.put(name, intent)
    }

    protected fun put(runnable: Runnable, name: String = runnable.toString()) {
        runnableInjector.put(name, runnable)
    }

    abstract fun inject()
}