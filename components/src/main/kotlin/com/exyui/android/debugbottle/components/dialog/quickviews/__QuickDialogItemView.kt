package com.exyui.android.debugbottle.components.dialog.quickviews

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.exyui.android.debugbottle.components.R

/**
 * Created by yuriel on 10/18/16.
 */
class __QuickDialogItemView
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
@JvmOverloads
constructor(
        context: Context,
        attr: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0)
    : RelativeLayout(context, attr, defStyleAttr, defStyleRes) {

    private val titleView by lazy { findViewById<TextView>(R.id.__dt_title)!! }
    private val contentView by lazy { findViewById<TextView>(R.id.__dt_description)!! }

    var title: CharSequence
        set(value) {
            titleView.text = value
        }
        get() = titleView.text

    var content: CharSequence?
        set(value) {
            if (value?.isNotEmpty() == true) {
                contentView.visibility = VISIBLE
                contentView.text = value
            } else {
                contentView.visibility = GONE
                contentView.text = ""
            }
        }
        get() = contentView.text

    private fun init(context: Context, attr: AttributeSet? = null, defStyleAttr: Int? = null, defStyleRes: Int? = null) {
        bindViews()
        initAttrs(context, attr, defStyleAttr?: 0, defStyleRes?: 0)
    }

    private fun bindViews() {
        inflate(context, R.layout.__custom_item_quick_dialog, this)
    }

    private fun initAttrs(context: Context, attr: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        val ta = context.theme.obtainStyledAttributes(attr, R.styleable.__DTListItem, defStyleAttr, defStyleRes)
        try {
            title = ta.getString(R.styleable.__DTListItem___dt_title)?: ""
            content = ta.getString(R.styleable.__DTListItem___dt_content)?: ""
        } finally {
            ta.recycle()
        }
    }

    init {
        init(context, attr, defStyleAttr, defStyleRes)
    }


}