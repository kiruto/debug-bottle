package com.exyui.android.debugbottle.components.bubbles

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.WindowManager
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.dp

/**
 * Created by yuriel on 9/22/16.
 */
internal class __BubbleLayout : __BubbleBaseLayout {
    constructor(context: Context): super(context) { init() }
    constructor(context: Context, attr: AttributeSet): super(context, attr) { init() }
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int): super(context, attr, defStyleAttr) { init() }
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attr, defStyleAttr, defStyleRes) { init() }

    private fun init() {
        animator = MoveAnimator()
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        initializeView()
    }

    private var initialTouchX: Float = 0.toFloat()
    private var initialTouchY: Float = 0.toFloat()
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var onBubbleRemoveListener: OnBubbleRemoveListener? = null
    private var onBubbleClickListener: OnBubbleClickListener? = null
    private val TOUCH_TIME_THRESHOLD = 150
    private var lastTouchDown: Long = 0
    private var animator: MoveAnimator? = null
    private var obsWidth: Int = 0
    private var shouldStickToWall = true

    fun setOnBubbleRemoveListener(listener: OnBubbleRemoveListener) {
        onBubbleRemoveListener = listener
    }

    fun setOnBubbleClickListener(listener: OnBubbleClickListener) {
        onBubbleClickListener = listener
    }

    fun setShouldStickToWall(shouldStick: Boolean) {
        this.shouldStickToWall = shouldStick
    }

    fun notifyBubbleRemoved() {
        if (onBubbleRemoveListener != null) {
            onBubbleRemoveListener!!.onBubbleRemoved(this)
        }
    }

    private fun initializeView() {
        isClickable = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        playAnimation()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = getViewParams().x
                    initialY = getViewParams().y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    playAnimationClickDown()
                    lastTouchDown = System.currentTimeMillis()
                    updateSize()
                    animator!!.stop()
                }
                MotionEvent.ACTION_MOVE -> {
                    val x: Int = (initialX + event.rawX - initialTouchX).toInt()
                    val y: Int = (initialY + event.rawY - initialTouchY).toInt()
                    getViewParams().x = x
                    getViewParams().y = y
                    windowManager.updateViewLayout(this, getViewParams())
                    getLayoutCoordinator()?.notifyBubblePositionChanged(this, x, y)
                }
                MotionEvent.ACTION_UP -> {
                    goToWall()
                    if (getLayoutCoordinator() != null) {
                        getLayoutCoordinator()?.notifyBubbleRelease(this)
                        playAnimationClickUp()
                    }
                    if (System.currentTimeMillis() - lastTouchDown < TOUCH_TIME_THRESHOLD) {
                        if (onBubbleClickListener != null) {
                            onBubbleClickListener!!.onBubbleClick(this)
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun playAnimation() {
        if (!isInEditMode) {
            val animator = AnimatorInflater.loadAnimator(context, R.animator.__bubble_shown_animator) as AnimatorSet
            animator.setTarget(this)
            animator.start()
        }
    }

    private fun playAnimationClickDown() {
        if (!isInEditMode) {
            val animator = AnimatorInflater.loadAnimator(context, R.animator.__bubble_down_click_animator) as AnimatorSet
            animator.setTarget(this)
            animator.start()
        }
    }

    private fun playAnimationClickUp() {
        if (!isInEditMode) {
            val animator = AnimatorInflater.loadAnimator(context, R.animator.__bubble_up_click_animator) as AnimatorSet
            animator.setTarget(this)
            animator.start()
        }
    }

    private fun updateSize() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        obsWidth = size.x - this.width

    }

    interface OnBubbleRemoveListener {
        fun onBubbleRemoved(bubble: __BubbleLayout)
    }

    interface OnBubbleClickListener {
        fun onBubbleClick(bubble: __BubbleLayout)
    }

    fun goToWall() {
        if (shouldStickToWall) {
            val middle = obsWidth / 2
            val nearestXWall = (if (getViewParams().x >= middle) obsWidth else 0).toFloat()
            animator!!.start(nearestXWall, getViewParams().y.toFloat())
        }
    }

    private fun move(deltaX: Float, deltaY: Float) {
        getViewParams().x += deltaX.toInt()
        getViewParams().y += deltaY.toInt()
        windowManager.updateViewLayout(this, getViewParams())
    }


    private inner class MoveAnimator : Runnable {
        private val handler = Handler(Looper.getMainLooper())
        private var destinationX: Float = 0.toFloat()
        private var destinationY: Float = 0.toFloat()
        private var startingTime: Long = 0

        internal fun start(x: Float, y: Float) {
            this.destinationX = x
            this.destinationY = y
            startingTime = System.currentTimeMillis()
            handler.post(this)
        }

        override fun run() {
            if (rootView != null && rootView.parent != null) {
                val progress = Math.min(1f, (System.currentTimeMillis() - startingTime) / 400f)
                val deltaX = (destinationX - getViewParams().x) * progress
                val deltaY = (destinationY - getViewParams().y) * progress
                move(deltaX, deltaY)
                if (progress < 1) {
                    handler.post(this)
                }
            }
        }

        internal fun stop() {
            handler.removeCallbacks(this)
        }
    }

}