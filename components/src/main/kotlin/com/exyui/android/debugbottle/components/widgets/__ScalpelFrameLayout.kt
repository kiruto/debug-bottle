package com.exyui.android.debugbottle.components.widgets

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import java.util.ArrayDeque
import java.util.BitSet
import java.util.Deque

import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.Style.STROKE
import android.graphics.Typeface.NORMAL
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_POINTER_UP
import android.view.MotionEvent.INVALID_POINTER_ID

/**
 * Created by yuriel on 9/2/16.
 *
 * Renders your view hierarchy as an interactive 3D visualization of layers.
 * <p>
 * Interactions supported:
 * <ul>
 * <li>Single touch: controls the rotation of the model.</li>
 * <li>Two finger vertical pinch: Adjust zoom.</li>
 * <li>Two finger horizontal pinch: Adjust layer spacing.</li>
 * </ul>
 *
 * <a href="https://raw.githubusercontent.com/JakeWharton/scalpel/master/scalpel/src/main/java/com/jakewharton/scalpel/ScalpelFrameLayout.java" />
 */
class __ScalpelFrameLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
        FrameLayout(context, attrs, defStyle) {
    private class LayeredView {
        internal var view: View? = null
        internal var layer: Int = 0
        internal fun set(view: View, layer: Int) {
            this.view = view
            this.layer = layer
        }

        internal fun clear() {
            view = null
            layer = -1
        }
    }

    private val viewBoundsRect = Rect()
    private val viewBorderPaint = Paint(ANTI_ALIAS_FLAG)
    private val camera = Camera()
    private val mMatrix = Matrix()
    private val location = IntArray(2)
    private val visibilities = BitSet(CHILD_COUNT_ESTIMATION)
    private val idNames = SparseArray<String>()
    private val layeredViewQueue = ArrayDeque<LayeredView>()
    private val layeredViewPool = object : Pool<LayeredView>(CHILD_COUNT_ESTIMATION) {
        protected override fun newObject(): LayeredView {
            return LayeredView()
        }
    }
    private val res: Resources
    private var density: Float = 0.toFloat()
    private var slop: Float = 0.toFloat()
    private var textOffset: Float = 0.toFloat()
    private var textSize: Float = 0.toFloat()
    /** Returns true when 3D view layer interaction is enabled. */
    /** Set whether or not the 3D view layer interaction is enabled. */
    var isLayerInteractionEnabled: Boolean = false
        set(enabled) {
            if (this.isLayerInteractionEnabled != enabled) {
                field = enabled
                setWillNotDraw(!enabled)
                invalidate()
            }
        }
    /** Returns true when view layers draw their contents. */
    var isDrawingViews = true
    /** Returns true when view layers draw their IDs. */
    var isDrawingIds: Boolean = false
    private var pointerOne = INVALID_POINTER_ID
    private var lastOneX: Float = 0.toFloat()
    private var lastOneY: Float = 0.toFloat()
    private var pointerTwo = INVALID_POINTER_ID
    private var lastTwoX: Float = 0.toFloat()
    private var lastTwoY: Float = 0.toFloat()
    private var multiTouchTracking = TRACKING_UNKNOWN
    private var mRotationY = ROTATION_DEFAULT_Y.toFloat()
    private var mRotationX = ROTATION_DEFAULT_X.toFloat()
    private var zoom = ZOOM_DEFAULT
    private var spacing = SPACING_DEFAULT.toFloat()
    /** Get the view border chrome color. */
    /** Set the view border chrome color. */
    var chromeColor: Int = 0
        set(color) {
            if (this.chromeColor != color) {
                viewBorderPaint.color = color
                field = color
                invalidate()
            }
        }
    /** Get the view border chrome shadow color. */
    /** Set the view border chrome shadow color. */
    var chromeShadowColor: Int = 0
        set(color) {
            if (this.chromeShadowColor != color) {
                viewBorderPaint.setShadowLayer(1F, -1F, 1F, color)
                field = color
                invalidate()
            }
        }

    init {
        res = context.resources
        density = context.resources.displayMetrics.density
        slop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
        textSize = TEXT_SIZE_DP * density
        textOffset = TEXT_OFFSET_DP * density
        chromeColor = CHROME_COLOR
        viewBorderPaint.style = STROKE
        viewBorderPaint.textSize = textSize
        chromeShadowColor = CHROME_SHADOW_COLOR
        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            viewBorderPaint.typeface = Typeface.create("sans-serif-condensed", NORMAL)
        }
    }

    /** Set whether the view layers draw their contents. When false, only wireframes are shown. */
    fun setDrawViews(drawViews: Boolean) {
        if (this.isDrawingViews != drawViews) {
            this.isDrawingViews = drawViews
            invalidate()
        }
    }

    /** Set whether the view layers draw their IDs. */
    fun setDrawIds(drawIds: Boolean) {
        if (this.isDrawingIds != drawIds) {
            this.isDrawingIds = drawIds
            invalidate()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return isLayerInteractionEnabled || super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(@SuppressWarnings("NullableProblems") event: MotionEvent): Boolean {
        if (!isLayerInteractionEnabled) {
            return super.onTouchEvent(event)
        }
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val index = if ((action == ACTION_DOWN)) 0 else event.actionIndex
                if (pointerOne == INVALID_POINTER_ID) {
                    pointerOne = event.getPointerId(index)
                    lastOneX = event.getX(index)
                    lastOneY = event.getY(index)
                    if (DEBUG) log("Got pointer 1! id: %s x: %s y: %s", pointerOne, lastOneY, lastOneY)
                } else if (pointerTwo == INVALID_POINTER_ID) {
                    pointerTwo = event.getPointerId(index)
                    lastTwoX = event.getX(index)
                    lastTwoY = event.getY(index)
                    if (DEBUG) log("Got pointer 2! id: %s x: %s y: %s", pointerTwo, lastTwoY, lastTwoY)
                } else {
                    if (DEBUG) log("Ignoring additional pointer. id: %s", event.getPointerId(index))
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (pointerTwo == INVALID_POINTER_ID) {
                    // Single pointer controlling 3D rotation.
                    var i = 0
                    val count = event.pointerCount
                    while (i < count) {
                        if (pointerOne == event.getPointerId(i)) {
                            val eventX = event.getX(i)
                            val eventY = event.getY(i)
                            val dx = eventX - lastOneX
                            val dy = eventY - lastOneY
                            val drx = 90 * (dx / width)
                            val dry = 90 * (-dy / height) // Invert Y-axis.
                            // An 'x' delta affects 'y' rotation and vise versa.
                            mRotationY = Math.min(Math.max(mRotationY + drx, ROTATION_MIN.toFloat()), ROTATION_MAX.toFloat())
                            mRotationX = Math.min(Math.max(mRotationX + dry, ROTATION_MIN.toFloat()), ROTATION_MAX.toFloat())
                            if (DEBUG) {
                                log("Single pointer moved (%s, %s) affecting rotation (%s, %s).", dx, dy, drx, dry)
                            }
                            lastOneX = eventX
                            lastOneY = eventY
                            invalidate()
                        }
                        i++
                    }
                } else {
                    // We know there's two pointers and we only care about pointerOne and pointerTwo
                    val pointerOneIndex = event.findPointerIndex(pointerOne)
                    val pointerTwoIndex = event.findPointerIndex(pointerTwo)
                    val xOne = event.getX(pointerOneIndex)
                    val yOne = event.getY(pointerOneIndex)
                    val xTwo = event.getX(pointerTwoIndex)
                    val yTwo = event.getY(pointerTwoIndex)
                    val dxOne = xOne - lastOneX
                    val dyOne = yOne - lastOneY
                    val dxTwo = xTwo - lastTwoX
                    val dyTwo = yTwo - lastTwoY
                    if (multiTouchTracking == TRACKING_UNKNOWN) {
                        val adx = Math.abs(dxOne) + Math.abs(dxTwo)
                        val ady = Math.abs(dyOne) + Math.abs(dyTwo)
                        if (adx > slop * 2 || ady > slop * 2) {
                            if (adx > ady) {
                                // Left/right movement wins. Track horizontal.
                                multiTouchTracking = TRACKING_HORIZONTALLY
                            } else {
                                // Up/down movement wins. Track vertical.
                                multiTouchTracking = TRACKING_VERTICALLY
                            }
                        }
                    }
                    if (multiTouchTracking == TRACKING_VERTICALLY) {
                        if (yOne >= yTwo) {
                            zoom += dyOne / height - dyTwo / height
                        } else {
                            zoom += dyTwo / height - dyOne / height
                        }
                        zoom = Math.min(Math.max(zoom, ZOOM_MIN), ZOOM_MAX)
                        invalidate()
                    } else if (multiTouchTracking == TRACKING_HORIZONTALLY) {
                        if (xOne >= xTwo) {
                            spacing += (dxOne / width * SPACING_MAX) - (dxTwo / width * SPACING_MAX)
                        } else {
                            spacing += (dxTwo / width * SPACING_MAX) - (dxOne / width * SPACING_MAX)
                        }
                        spacing = Math.min(Math.max(spacing, SPACING_MIN.toFloat()), SPACING_MAX.toFloat())
                        invalidate()
                    }
                    if (multiTouchTracking != TRACKING_UNKNOWN) {
                        lastOneX = xOne
                        lastOneY = yOne
                        lastTwoX = xTwo
                        lastTwoY = yTwo
                    }
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val index = if ((action != ACTION_POINTER_UP)) 0 else event.actionIndex
                val pointerId = event.getPointerId(index)
                if (pointerOne == pointerId) {
                    // Shift pointer two (real or invalid) up to pointer one.
                    pointerOne = pointerTwo
                    lastOneX = lastTwoX
                    lastOneY = lastTwoY
                    if (DEBUG) log("Promoting pointer 2 (%s) to pointer 1.", pointerTwo)
                    // Clear pointer two and tracking.
                    pointerTwo = INVALID_POINTER_ID
                    multiTouchTracking = TRACKING_UNKNOWN
                } else if (pointerTwo == pointerId) {
                    if (DEBUG) log("Lost pointer 2 (%s).", pointerTwo)
                    pointerTwo = INVALID_POINTER_ID
                    multiTouchTracking = TRACKING_UNKNOWN
                }
            }
        }
        return true
    }

    override fun draw(@SuppressWarnings("NullableProblems") canvas: Canvas) {
        if (!isLayerInteractionEnabled) {
            super.draw(canvas)
            return
        }
        getLocationInWindow(location)
        val x = location[0].toFloat()
        val y = location[1].toFloat()
        val saveCount = canvas.save()
        val cx = width / 2f
        val cy = height / 2f
        camera.save()
        camera.rotate(mRotationX, mRotationY, 0F)
        camera.getMatrix(mMatrix)
        camera.restore()
        mMatrix.preTranslate(-cx, -cy)
        mMatrix.postTranslate(cx, cy)
        canvas.concat(mMatrix)
        canvas.scale(zoom, zoom, cx, cy)
        if (!layeredViewQueue.isEmpty()) {
            throw AssertionError("View queue is not empty.")
        }
        // We don't want to be rendered so seed the queue with our children.
        run {
            var i = 0
            val count = childCount
            while (i < count) {
                val layeredView = layeredViewPool.obtain()
                layeredView.set(getChildAt(i), 0)
                layeredViewQueue.add(layeredView)
                i++
            }
        }
        while (!layeredViewQueue.isEmpty()) {
            val layeredView = layeredViewQueue.removeFirst()
            val view = layeredView.view
            val layer = layeredView.layer
            // Restore the object to the pool for use later.
            layeredView.clear()
            layeredViewPool.restore(layeredView)
            // Hide any visible children.
            if (view is ViewGroup) {
                val viewGroup = view
                visibilities.clear()
                var i = 0
                val count = viewGroup.childCount
                while (i < count) {
                    val child = viewGroup.getChildAt(i)
                    //noinspection ConstantConditions
                    if (child.visibility == VISIBLE) {
                        visibilities.set(i)
                        child.visibility = INVISIBLE
                    }
                    i++
                }
            }
            val viewSaveCount = canvas.save()
            // Scale the layer index translation by the rotation amount.
            val translateShowX = mRotationY / ROTATION_MAX
            val translateShowY = mRotationX / ROTATION_MAX
            val tx = layer.toFloat() * spacing * density * translateShowX
            val ty = layer.toFloat() * spacing * density * translateShowY
            canvas.translate(tx, -ty)
            view?.getLocationInWindow(location)
            canvas.translate(location[0] - x, location[1] - y)
            viewBoundsRect.set(0, 0, view?.width?: 0, view?.height?: 0)
            canvas.drawRect(viewBoundsRect, viewBorderPaint)
            if (isDrawingViews) {
                view?.draw(canvas)
            }
            if (isDrawingIds) {
                val id = view?.id
                if (id != NO_ID) {
                    canvas.drawText(nameForId(id?: 0), textOffset, textSize, viewBorderPaint)
                }
            }
            canvas.restoreToCount(viewSaveCount)
            // Restore any hidden children and queue them for later drawing.
            if (view is ViewGroup) {
                val viewGroup = view
                var i = 0
                val count = viewGroup.childCount
                while (i < count) {
                    if (visibilities.get(i)) {
                        val child = viewGroup.getChildAt(i)
                        //noinspection ConstantConditions
                        child.visibility = VISIBLE
                        val childLayeredView = layeredViewPool.obtain()
                        childLayeredView.set(child, layer + 1)
                        layeredViewQueue.add(childLayeredView)
                    }
                    i++
                }
            }
        }
        canvas.restoreToCount(saveCount)
    }

    private fun nameForId(id: Int): String {
        var name = idNames.get(id)
        if (name == null) {
            try {
                name = res.getResourceEntryName(id)
            } catch (e: NotFoundException) {
                name = String.format("0x%8x", id)
            }
            idNames.put(id, name)
        }
        return name
    }

    private abstract class Pool<T> internal constructor(initialSize: Int) {
        private val pool: Deque<T>

        init {
            pool = ArrayDeque(initialSize)
            for (i in 0..initialSize - 1) {
                pool.addLast(newObject())
            }
        }

        internal fun obtain(): T {
            return if (pool.isEmpty()) newObject() else pool.removeLast()
        }

        internal fun restore(instance: T) {
            pool.addLast(instance)
        }

        protected abstract fun newObject(): T
    }

    companion object {
        private val TRACKING_UNKNOWN = 0
        private val TRACKING_VERTICALLY = 1
        private val TRACKING_HORIZONTALLY = -1
        private val ROTATION_MAX = 60
        private val ROTATION_MIN = -ROTATION_MAX
        private val ROTATION_DEFAULT_X = -10
        private val ROTATION_DEFAULT_Y = 15
        private val ZOOM_DEFAULT = 0.6f
        private val ZOOM_MIN = 0.33f
        private val ZOOM_MAX = 2f
        private val SPACING_DEFAULT = 25
        private val SPACING_MIN = 10
        private val SPACING_MAX = 100
        private val CHROME_COLOR = 0xFF888888.toInt()
        private val CHROME_SHADOW_COLOR = 0xFF000000.toInt()
        private val TEXT_OFFSET_DP = 2
        private val TEXT_SIZE_DP = 10
        private val CHILD_COUNT_ESTIMATION = 25
        private val DEBUG = false
        private fun log(message: String, vararg args: Any) {
            Log.d("Scalpel", String.format(message, *args))
        }
    }
}