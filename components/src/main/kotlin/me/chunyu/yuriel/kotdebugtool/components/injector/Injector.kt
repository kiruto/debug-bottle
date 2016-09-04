package me.chunyu.yuriel.kotdebugtool.components.injector

import android.content.Intent

/**
 * Created by yuriel on 8/15/16.
 */
abstract class Injector {
    private val intentInjector = IntentInjector
    private val runnableInjector = RunnableInjector

    protected fun add(intent: Intent, name: String = intent.toString()) {
        intentInjector.put(name, intent)
    }

    protected fun add(runnable: Runnable, name: String = runnable.toString()) {
        runnableInjector.put(name, runnable)
    }

    abstract fun inject()
}