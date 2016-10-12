package com.exyui.android.debugbottle.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Created by yuriel on 8/25/16.
 */
class __MoreDetailsView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val mIconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mFolding = true

    init {
        mIconPaint.strokeWidth = LeakCanaryUi.dpToPixel(2f, resources)
        mIconPaint.color = LeakCanaryUi.ROOT_COLOR
    }


    override fun onDraw(canvas: Canvas) {
        val width = width
        val height = height
        val halfHeight = height / 2
        val halfWidth = width / 2

        canvas.drawLine(0f, halfHeight.toFloat(), width.toFloat(), halfHeight.toFloat(), mIconPaint)
        if (mFolding) {
            canvas.drawLine(halfWidth.toFloat(), 0f, halfWidth.toFloat(), height.toFloat(), mIconPaint)
        }
    }

    fun setFolding(folding: Boolean) {
        if (folding != this.mFolding) {
            this.mFolding = folding
            invalidate()
        }
    }
}