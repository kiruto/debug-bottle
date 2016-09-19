package com.exyui.android.debugbottle.components.floating

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import android.widget.Button
import com.exyui.android.debugbottle.components.R
import kotlin.reflect.KClass

/**
 * Created by yuriel on 9/19/16.
 */
internal abstract class DTDragFloatingViewMgr {

    protected var context: Context? = null
    protected var windowMgr: WindowManager? = null
    protected var floatingView: ViewGroup? = null
    abstract val title: String
    abstract val bindingService: KClass<out Service>

    fun setupWith(context: Context) {
        this.context = context
        windowMgr = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun showFloatingView(): FloatingViewHolder {
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
        floatingView = root
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

        action.text = title

        action.setOnClickListener { v ->
            onClick(v)
        }

        return FloatingViewHolder(root, drag, action)
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
            if (serviceList[i].service.className.equals(bindingService.java.name) == true) {
                return true
            }
        }
        return false
    }

    fun releaseView() {
        if (null != floatingView) {
            windowMgr?.removeView(floatingView)
            floatingView = null
        }
    }

    fun releaseContext(context: Context) {
        if (context === this.context) {
            this.context = null
        }
    }

    abstract fun onClick(v: View)

    data class FloatingViewHolder(
            val root: ViewGroup,
            val drag: View,
            val action: View
    )
}