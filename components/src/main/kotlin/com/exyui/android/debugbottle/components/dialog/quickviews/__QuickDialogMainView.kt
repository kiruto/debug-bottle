package com.exyui.android.debugbottle.components.dialog.quickviews

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.ScrollView
import com.exyui.android.debugbottle.components.R

/**
 * Created by yuriel on 10/13/16.
 */
class __QuickDialogMainView: ScrollView {
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

    private fun init(context: Context, attr: AttributeSet? = null, defStyleAttr: Int? = null, defStyleRes: Int? = null) {
        bindViews()
        initAttrs(context, attr, defStyleAttr?: 0, defStyleRes?: 0)
    }

    private fun bindViews() {
        inflate(context, R.layout.__fragment_quick_main, this)
    }

    private fun initAttrs(context: Context, attr: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
//        val ta = context.theme.obtainStyledAttributes(attr, R.styleable.__DTListItem, defStyleAttr, defStyleRes)
//        try {
//            title = ta.getString(R.styleable.__DTListItem___dt_title)?: ""
//            content = ta.getString(R.styleable.__DTListItem___dt_content)?: ""
//            isSmart = ta.getBoolean(R.styleable.__DTListItem___dt_smart, false)
//        } finally {
//            ta.recycle()
//        }
    }
}