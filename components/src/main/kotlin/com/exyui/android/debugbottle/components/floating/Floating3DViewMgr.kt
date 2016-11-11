//package com.exyui.android.debugbottle.components.floating
//
//import android.app.Activity
//import android.app.ActivityManager
//import android.app.Service
//import android.content.Context
//import android.graphics.PixelFormat
//import android.view.*
//import android.widget.Button
//import com.exyui.android.debugbottle.components.DTActivityManager
//import com.exyui.android.debugbottle.components.R
//import com.exyui.android.debugbottle.components.widgets.__ScalpelFrameLayout
//import kotlin.reflect.KClass
//
///**
// * Created by yuriel on 9/2/16.
// */
//@Deprecated(
//        message = "use __3DViewBubble instead",
//        replaceWith = ReplaceWith("__3DViewBubble", "com.exyui.android.debugbottle.components.bubbles.services.__3DViewBubble")
//)
//internal object Floating3DViewMgr: DTDragFloatingViewMgr() {
//
//    override val title: String = "3D"
//    @Suppress("DEPRECATION")
//    override val bindingService = Floating3DService::class
//
//    override fun onClick(v: View) {
//        val activity = DTActivityManager.topActivity
//        activity?: return
//        attachActivityTo3DView(activity)
//    }
//
//    private fun attachActivityTo3DView(activity: Activity) {
//        val layout: __ScalpelFrameLayout
//        val decorView: ViewGroup = activity.window.decorView as ViewGroup
//        if (decorView.findViewById(R.id.__dt_scalpel_frame_layout) == null) {
//            layout = __ScalpelFrameLayout(activity)
//            layout.id = R.id.__dt_scalpel_frame_layout
//            val rootView = decorView.getChildAt(0)
//
//            decorView.removeView(rootView)
//            layout.addView(rootView)
//            decorView.addView(layout)
//        } else {
//            layout = decorView.findViewById(R.id.__dt_scalpel_frame_layout) as __ScalpelFrameLayout
//        }
//
//        if (!layout.isLayerInteractionEnabled) {
//            layout.isLayerInteractionEnabled = true
//            layout.setDrawIds(true)
//        }else {
//            layout.isLayerInteractionEnabled = false
//        }
//    }
//
//
//}