package com.exyui.android.debugbottle.components.bubbles.services

import android.content.Context
import android.content.Intent
import com.exyui.android.debugbottle.components.bubbles.__BubbleLayout
import com.exyui.android.debugbottle.components.bubbles.__BubblesManager
import com.exyui.android.debugbottle.components.bubbles.__OnBubbleStatusChangeCallback

/**
 * Created by yuriel on 9/23/16.
 */
internal abstract class __DTBubble(protected val bubblesManager: __BubblesManager) {
    abstract val TAG: String
    protected var view: __BubbleLayout? = null
    open val position = Pair(60, 20)
    private var appContext: Context? = null

    companion object {
        val INTENT_ACTION = "com.exyui.android.debugbottle.components.bubbles.services.DTBubble"
        val KEY_IS_RUNNING = "KEY_IS_RUNNING"
        val KEY_TAG = "KEY_TAG"
    }

    fun create(context: Context) { onCreate(context.applicationContext) }

    fun destroy(context: Context) { onDestroy(context.applicationContext) }

    protected open fun onCreate(context: Context) {
        view = onCreateView(context)
        view?.attach(position.first, position.second)
        appContext = context
        // send a broadcast to notify ui changes
        notify(appContext?: return)
    }

    protected open fun onDestroy(context: Context) {
        view?.remove()
    }

    // Send a broadcast to update ui
    protected fun notify(context: Context? = appContext) {
        val intent = Intent(INTENT_ACTION)
        intent.putExtra(KEY_TAG, TAG)
        intent.putExtra(KEY_IS_RUNNING, isRunning())
        context?.sendBroadcast(intent)
    }

    protected fun __BubbleLayout.remove() {
        bubblesManager.removeBubble(this)
    }

    protected fun __BubbleLayout.attach(x: Int, y: Int) {
        bubblesManager.addBubble(this, x, y)
    }

    abstract fun onCreateView(context: Context): __BubbleLayout
    abstract fun isRunning(): Boolean
}