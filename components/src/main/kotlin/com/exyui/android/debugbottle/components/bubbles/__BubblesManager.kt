package com.exyui.android.debugbottle.components.bubbles

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

/**
 * Created by yuriel on 9/22/16.
 */
internal object __BubblesManager {
    var bounded: Boolean = false
        private set
    private var bubblesService: __BubblesService? = null
    private var trashLayoutResourceId: Int = 0
    private var listener: __OnInitializedCallback? = null

    private val bubbleServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as __BubblesService.BubblesServiceBinder
            this@__BubblesManager.bubblesService = binder.service
            configureBubblesService()
            bounded = true
            listener?.onInitialized()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            bounded = false
        }
    }

    private fun configureBubblesService() {
        bubblesService!!.addTrash(trashLayoutResourceId)
    }

    fun initialize(context: Context) {
        if (bounded) {
            listener?.onInitialized()
        } else {
            context.bindService(Intent(context, __BubblesService::class.java),
                    bubbleServiceConnection,
                    Context.BIND_AUTO_CREATE)
        }
    }

    fun recycle(context: Context) {
        context.unbindService(bubbleServiceConnection)
    }

    fun addBubble(bubble: __BubbleLayout, x: Int, y: Int) {
        if (bounded) {
            bubblesService!!.addBubble(bubble, x, y)
        }
    }

    fun removeBubble(bubble: __BubbleLayout) {
        if (bounded) {
            bubblesService!!.removeBubble(bubble)
        }
    }

    class Builder() {
        private val bubblesManager: __BubblesManager

        init {
            this.bubblesManager = __BubblesManager
        }

        fun setInitializationCallback(listener: __OnInitializedCallback): Builder {
            bubblesManager.listener = listener
            return this
        }

        fun setTrashLayout(trashLayoutResourceId: Int): Builder {
            bubblesManager.trashLayoutResourceId = trashLayoutResourceId
            return this
        }

        fun build(): __BubblesManager {
            return bubblesManager
        }
    }
}