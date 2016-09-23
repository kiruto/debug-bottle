package com.exyui.android.debugbottle.components.scalpel

import android.app.Service
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.bubbles.__BubbleLayout
import com.exyui.android.debugbottle.components.bubbles.__BubblesManager

/**
 * Created by yuriel on 9/23/16.
 */
internal class __ScalpelService: Service() {
    private val TAG = "__ScalpelService"
    private lateinit var bubblesMgr: __BubblesManager
    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        bubblesMgr = __BubblesManager.Builder(this)
                .build()
        bubblesMgr.initialize()
        val bubbleView = LayoutInflater.from(this).inflate(R.layout.__bubble_3d, null) as __BubbleLayout
        val btn = bubbleView.findViewById(R.id.__3d_view_btn)
        btn.setOnClickListener {
            Log.d(TAG, "clicked")
        }
        bubblesMgr.addBubble(bubbleView, 60, 20)
    }

    override fun onDestroy() {
        super.onDestroy()
        bubblesMgr.recycle()
    }
}