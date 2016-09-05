package me.chunyu.yuriel.kotdebugtool.components.floating

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import android.widget.Button
import me.chunyu.yuriel.kotdebugtool.components.DTActivityManager
import me.chunyu.yuriel.kotdebugtool.components.R
import me.chunyu.yuriel.kotdebugtool.components.widgets.__ScalpelFrameLayout

/**
 * Created by yuriel on 9/2/16.
 */
internal object FloatingViewMgr {

    private var context: Context? = null
    private var windowMgr: WindowManager? = null
    private var floating3DView: ViewGroup? = null

    fun setupWith(context: Context) {
        this.context = context
        windowMgr = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun show3DViewFloating(): Floating3DViewHolder {
        val wmParams = WindowManager.LayoutParams()
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE
        wmParams.format = PixelFormat.RGBA_8888
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        wmParams.gravity = Gravity.LEFT or Gravity.TOP
        wmParams.x = 0
        wmParams.y = 0

        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        /*
        wmParams.width = 200
        wmParams.height = 80
        */

        val inflater = LayoutInflater.from(context?.applicationContext)

        val root = inflater.inflate(R.layout.__dt_float_layout, null) as ViewGroup
        val action = root.findViewById(R.id.__dt_float_obj) as Button
        val drag = root.findViewById(R.id.__dt_drag)


        windowMgr?.addView(root, wmParams)
        floating3DView = root
        root.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        drag.setOnTouchListener { v, event ->
            wmParams.x = (event.rawX - action.measuredWidth / 2).toInt()
            //Log.i(TAG, "RawX" + event.rawX)
            //Log.i(TAG, "X" + event.rawX)
            wmParams.y = (event.rawY - action.measuredHeight / 2 - 200).toInt()
            //Log.i(TAG, "RawY" + event.rawX)
            //Log.i(TAG, "Y" + event.rawY)
            //刷新
            windowMgr?.updateViewLayout(root, wmParams)
            false
        }

        action.setOnClickListener { v ->
            val activity = DTActivityManager.topActivity
            activity?: return@setOnClickListener
            attachActivityTo3DView(activity)
        }

        return Floating3DViewHolder(root, drag, action)
    }

    fun release3DView() {
        if (null != floating3DView) {
            windowMgr?.removeView(floating3DView)
            floating3DView = null
        }
    }

    fun releaseContext(context: Context) {
        if (context === this.context) {
            this.context = null
        }
    }

    fun isFloatingWindowRunning(): Boolean {
        val activityManager: ActivityManager
        try {
            activityManager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        } catch (e: Exception) {
            return false
        }
        val serviceList = activityManager.getRunningServices(30)

        if (serviceList.size <= 0) {
            return false
        }

        for (i in 0..serviceList.size - 1) {
            if (serviceList[i].service.className.equals(__FloatingService::class.java.name) == true) {
                return true
            }
        }
        return false
    }

    private fun attachActivityTo3DView(activity: Activity) {
        val layout: __ScalpelFrameLayout
        val decorView: ViewGroup = activity.window.decorView as ViewGroup
        if (decorView.findViewById(R.id.__dt_scalpel_frame_layout) == null) {
            layout = __ScalpelFrameLayout(activity)
            layout.id = R.id.__dt_scalpel_frame_layout
            val rootView = decorView.getChildAt(0)

            decorView.removeView(rootView)
            layout.addView(rootView)
            decorView.addView(layout)
        } else {
            layout = decorView.findViewById(R.id.__dt_scalpel_frame_layout) as __ScalpelFrameLayout
        }

        if (!layout.isLayerInteractionEnabled) {
            layout.isLayerInteractionEnabled = true
            layout.setDrawIds(true)
        }else {
            layout.isLayerInteractionEnabled = false
        }
    }

    private fun remove3DView() {

    }

    data class Floating3DViewHolder (
            val root: ViewGroup,
            val drag: View,
            val action: View
    )
}