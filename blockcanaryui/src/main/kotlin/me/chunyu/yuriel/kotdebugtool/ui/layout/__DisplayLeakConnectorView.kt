package me.chunyu.yuriel.kotdebugtool.ui.layout

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

import android.graphics.Bitmap.Config.ARGB_8888

/**
 * Created by yuriel on 8/9/16.
 */
class __DisplayLeakConnectorView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    enum class Type {
        START, NODE, END
    }

    private var type: Type? = null
    private var cache: Bitmap? = null

    init {

        type = Type.NODE
    }

    @SuppressWarnings("SuspiciousNameCombination")
    override fun onDraw(canvas: Canvas) {
        val width = width
        val height = height

        if (cache != null && (cache!!.width != width || cache!!.height != height)) {
            cache!!.recycle()
            cache = null
        }

        if (cache == null) {
            cache = Bitmap.createBitmap(width, height, ARGB_8888)

            val cacheCanvas = Canvas(cache!!)

            val halfWidth = width / 2f
            val halfHeight = height / 2f
            val thirdWidth = width / 3f

            val strokeSize = LeakCanaryUi.dpToPixel(4f, resources)

            iconPaint.strokeWidth = strokeSize
            rootPaint.strokeWidth = strokeSize

            when (type) {
                __DisplayLeakConnectorView.Type.NODE -> {
                    cacheCanvas.drawLine(halfWidth, 0f, halfWidth, height.toFloat(), iconPaint)
                    cacheCanvas.drawCircle(halfWidth, halfHeight, halfWidth, iconPaint)
                    cacheCanvas.drawCircle(halfWidth, halfHeight, thirdWidth, clearPaint)
                }
                __DisplayLeakConnectorView.Type.START -> {
                    val radiusClear = halfWidth - strokeSize / 2f
                    cacheCanvas.drawRect(0f, 0f, width.toFloat(), radiusClear, rootPaint)
                    cacheCanvas.drawCircle(0f, radiusClear, radiusClear, clearPaint)
                    cacheCanvas.drawCircle(width.toFloat(), radiusClear, radiusClear, clearPaint)
                    cacheCanvas.drawLine(halfWidth, 0f, halfWidth, halfHeight, rootPaint)
                    cacheCanvas.drawLine(halfWidth, halfHeight, halfWidth, height.toFloat(), iconPaint)
                    cacheCanvas.drawCircle(halfWidth, halfHeight, halfWidth, iconPaint)
                    cacheCanvas.drawCircle(halfWidth, halfHeight, thirdWidth, clearPaint)
                }
                else -> {
                    cacheCanvas.drawLine(halfWidth, 0f, halfWidth, halfHeight, iconPaint)
                    cacheCanvas.drawCircle(halfWidth, halfHeight, thirdWidth, leakPaint)
                }
            }
        }
        canvas.drawBitmap(cache!!, 0f, 0f, null)
    }

    fun setType(type: Type) {
        if (type != this.type) {
            this.type = type
            if (cache != null) {
                cache!!.recycle()
                cache = null
            }
            invalidate()
        }
    }

    companion object {

        private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val rootPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val leakPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        init {
            iconPaint.color = LeakCanaryUi.LIGHT_GREY
            rootPaint.color = LeakCanaryUi.ROOT_COLOR
            leakPaint.color = LeakCanaryUi.LEAK_COLOR
            clearPaint.color = Color.TRANSPARENT
            clearPaint.xfermode = LeakCanaryUi.CLEAR_XFER_MODE
        }
    }
}