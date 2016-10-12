package com.exyui.android.debugbottle.components.widgets

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.widget.SwitchCompat
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.sp

/**
 * Created by yuriel on 9/21/16.
 */
internal class DTListItemSwitch : LinearLayout {

    var title: String = ""
        set(value) {
            field = value
            titleView.text = field
            invalidate()
            requestLayout()
        }
    var content: String = ""
        set(value) {
            field = value
            contentView.text = field
            invalidate()
            requestLayout()
        }

    var isChecked: Boolean
        get() = switchView.isChecked
        set(value) {
            switchView.isChecked = value
        }

    var isSmart: Boolean = false
        set(value) {
            if (value) {
                contentView.visibility = View.GONE
            } else {
                contentView.visibility = View.VISIBLE
            }
            field = value
        }

    /**
     * Switcher enabled
     */
    var enable: Boolean
        get() = switchView.isEnabled
        set(value) {
            switchView.isEnabled = value
        }

    lateinit var titleView: TextView
    lateinit var contentView: TextView
    lateinit var switchView: SwitchCompat

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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    fun setOnCheckedChangeListener(listener: (v: View, isChecked: Boolean) -> Unit) {
        switchView.setOnCheckedChangeListener(listener)
    }

    private fun bindViews() {
        inflate(context, R.layout.__custom_list_item_swicher, this)
        titleView = findViewById(R.id.__dt_title) as TextView
        contentView = findViewById(R.id.__dt_content) as TextView
        switchView = findViewById(R.id.__dt_switcher) as SwitchCompat
    }

    private fun init(context: Context, attr: AttributeSet? = null, defStyleAttr: Int? = null, defStyleRes: Int? = null) {
        bindViews()
        initAttrs(context, attr, defStyleAttr?: 0, defStyleRes?: 0)
    }

    private fun initAttrs(context: Context, attr: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        val ta = context.theme.obtainStyledAttributes(attr, R.styleable.__DTListItem, defStyleAttr, defStyleRes)
        try {
            title = ta.getString(R.styleable.__DTListItem___dt_title)?: ""
            content = ta.getString(R.styleable.__DTListItem___dt_content)?: ""
            isSmart = ta.getBoolean(R.styleable.__DTListItem___dt_smart, false)
        } finally {
            ta.recycle()
        }
    }
}