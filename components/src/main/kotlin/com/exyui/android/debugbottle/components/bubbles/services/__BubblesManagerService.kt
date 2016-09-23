package com.exyui.android.debugbottle.components.bubbles.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.bubbles.__BubbleLayout
import com.exyui.android.debugbottle.components.bubbles.__BubblesManager

/**
 * Created by yuriel on 9/23/16.
 */
internal class __BubblesManagerService : Service() {
    private val TAG = "__BubblesManagerService"
    private lateinit var bubblesMgr: __BubblesManager
    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        bubblesMgr = __BubblesManager.Builder()
                .setTrashLayout(R.layout.__bubble_trash)
                .build()
        bubblesMgr.initialize(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        bubblesMgr.recycle(this)
    }

    fun __BubbleLayout.remove() {
        bubblesMgr.removeBubble(this)
    }
}