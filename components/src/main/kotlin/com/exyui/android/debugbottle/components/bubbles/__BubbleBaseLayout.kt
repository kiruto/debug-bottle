package com.exyui.android.debugbottle.components.bubbles

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.WindowManager
import android.widget.FrameLayout

/**
 * Created by yuriel on 9/22/16.
 */
internal open class __BubbleBaseLayout : FrameLayout {

    constructor(context: Context): super(context)
    constructor(context: Context, attr: AttributeSet): super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int): super(context, attr, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("NewApi")
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attr, defStyleAttr, defStyleRes)

    lateinit var windowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams
    private var layoutCoordinator: __BubblesLayoutCoordinator? = null

    fun setLayoutCoordinator(layoutCoordinator: __BubblesLayoutCoordinator?) {
        this.layoutCoordinator = layoutCoordinator
    }

    fun getLayoutCoordinator(): __BubblesLayoutCoordinator? {
        return layoutCoordinator
    }

    fun setViewParams(params: WindowManager.LayoutParams) {
        this.params = params
    }

    fun getViewParams(): WindowManager.LayoutParams {
        return this.params
    }
}