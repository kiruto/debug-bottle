package com.exyui.android.debugbottle.components.floating.frame

import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import com.exyui.android.debugbottle.components.R

/**
 * Created by yuriel on 9/30/16.
 */
internal class __TaktFrame {
    private var rootView: View? = null

    fun create(context: Context) {
        if (null != rootView) return
        val app = context.applicationContext
        val wmParams = WindowManager.LayoutParams()
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE
        wmParams.format = PixelFormat.RGBA_8888
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        wmParams.gravity = Gravity.LEFT or Gravity.TOP
        wmParams.x = 0
        wmParams.y = 0

        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        val inflater = LayoutInflater.from(app)

        rootView = inflater.inflate(R.layout.__dt_float_frame, null) as ViewGroup
        bindViews()
    }

    fun destroy() {
        rootView?: return

    }

    private fun bindViews() {

    }
}