package com.exyui.android.debugbottle.components.floating.frame

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Handler
import android.view.*
import android.widget.TextView
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.displayperformance.__DTHeartBeat
import com.exyui.android.debugbottle.core.__FrameRecorder
import java.text.DecimalFormat

/**
 * Created by yuriel on 9/30/16.
 */
internal object __FloatFrame {
    private val TAG = "__FloatFrame"

    private var rootView: View? = null
    private var frameView: TextView? = null
    private var statusView: TextView? = null

    private lateinit var heartBeat: __DTHeartBeat
    private val decimal = DecimalFormat("#.0' fps'")

    private val frameSetter by lazy {
        object: Runnable {
            override fun run() {
                //val fps = __FrameRecorder.fps.toString()
                //Log.d(TAG, "scan fps: $fps")
                //frameView?.text = fps
                Handler().postDelayed(this, 1000)
            }
        }
    }

    fun start(context: Context) {
        if (null != rootView) return
        val app = context.applicationContext
        val wmParams = WindowManager.LayoutParams()
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE
        wmParams.format = PixelFormat.RGBA_8888
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        wmParams.gravity = Gravity.RIGHT or Gravity.BOTTOM
        wmParams.x = 0
        wmParams.y = 0

        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        val inflater = LayoutInflater.from(app)

        rootView = inflater.inflate(R.layout.__dt_float_frame, null) as ViewGroup
        bindViews()

        val mgr = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mgr.addView(rootView, wmParams)

        Handler().post(frameSetter)
        heartBeat = __DTHeartBeat()
        heartBeat.start()
        heartBeat.addListener {
            frameView?.text = decimal.format(it)
        }

        __FrameRecorder.listener = {
            if (it <= 16) {
                statusView?.idle()
            } else if (it <= 30) {
                statusView?.warn()
            } else {
                statusView?.busy()
            }
        }
    }

    fun stop(context: Context) {
        rootView?: return
        heartBeat.stop()
        val mgr = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        Handler().removeCallbacks(frameSetter)
        mgr.removeView(rootView)
        rootView = null
        frameView = null
        statusView = null
    }

    private fun bindViews() {
        frameView = rootView?.findViewById(R.id.__dt_frame) as TextView
        statusView = rootView?.findViewById(R.id.__dt_status) as TextView
    }

    private fun TextView.warn() {
        setTextColor(Color.parseColor("#FFEB3B"))
        setText(R.string.__warn)
    }

    private fun TextView.idle() {
        setTextColor(Color.parseColor("#4CAF50"))
        setText(R.string.__idle)
    }

    private fun TextView.busy() {
        setTextColor(Color.parseColor("#F44336"))
        setText(R.string.__busy)
    }
}