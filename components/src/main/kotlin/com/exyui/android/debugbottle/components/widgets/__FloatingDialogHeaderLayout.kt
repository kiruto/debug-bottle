package com.exyui.android.debugbottle.components.widgets

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.exyui.android.debugbottle.components.R

/**
 * Created by yuriel on 9/27/16.
 */
internal class __FloatingDialogHeaderLayout : LinearLayout {

    var title: String = ""
        set(value) {
            field = value
            titleView.text = field
            invalidate()
            requestLayout()
        }

    lateinit var titleView: TextView
    lateinit var actionView: ImageView
    lateinit var closeView: View

    constructor(context: Context): super(context) {
        init(context)
    }
    constructor(context: Context, attr: AttributeSet): super(context, attr) {
        init(context, attr)
    }
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int): super(context, attr, defStyleAttr) {
        init(context, attr, defStyleAttr)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("NewApi")
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attr, defStyleAttr, defStyleRes) {
        init(context, attr, defStyleAttr, defStyleRes)
    }

    private fun bindViews() {
        inflate(context, R.layout.__custom_floating_header, this)
        titleView = findViewById(R.id.__dt_header_title) as TextView
        actionView = findViewById(R.id.__dt_header_action) as ImageView
        closeView = findViewById(R.id.__dt_header_close)
    }

    private fun init(context: Context, attr: AttributeSet? = null, defStyleAttr: Int? = null, defStyleRes: Int? = null) {
        bindViews()
        initAttrs(context, attr, defStyleAttr?: 0, defStyleRes?: 0)
    }

    private fun initAttrs(context: Context, attr: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        val ta = context.theme.obtainStyledAttributes(attr, R.styleable.__DTListItem, defStyleAttr, defStyleRes)
        try {
            title = ta.getString(R.styleable.__DTListItem___dt_title)?: ""
        } finally {
            ta.recycle()
        }
    }

    fun setAction(listener: (View) -> Unit) {
        actionView.setOnClickListener(listener)
    }

    fun setActionIcon(@DrawableRes res: Int) {
        actionView.setImageResource(res)
    }

    fun setClose(listener: (View) -> Unit) {
        closeView.setOnClickListener(listener)
    }
}