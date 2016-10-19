package com.exyui.android.debugbottle.components.dialog.quickviews

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.injector.QuickEntry
import com.exyui.android.debugbottle.components.openInBrowser

/**
 * Created by yuriel on 10/13/16.
 */
class __QuickDialogMainView: ScrollView {
//    constructor(context: Context): super(context) {
//        init(context)
//    }
//    constructor(context: Context, attr: AttributeSet): super(context, attr) {
//        init(context, attr)
//    }
//    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int): super(context, attr, defStyleAttr) {
//        init(context, attr, defStyleAttr)
//    }

    private val rootChild by lazy { findViewById(R.id.__dt_content) as ViewGroup }
    private val emptyView by lazy {
        val result = findViewById(R.id.__dt_empty)
        result.setOnClickListener {
            val url = "https://github.com/kiruto/debug-bottle/blob/1.0.1/demo/src/main/kotlin/me/chunyu/dev/yuriel/kotdebugtool/ContentInjector.kt"
            (context as Activity).openInBrowser(url)
        }
        result
    }
    private val contents by lazy { findViewById(R.id.__dt_content) }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("NewApi")
    @JvmOverloads
    constructor(context: Context,
                attr: AttributeSet? = null,
                defStyleAttr: Int = 0,
                defStyleRes: Int = 0) : super(context, attr, defStyleAttr, defStyleRes) {
        init(context, attr, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attr: AttributeSet? = null, defStyleAttr: Int? = null, defStyleRes: Int? = null) {
        bindViews()
        initAttrs(context, attr, defStyleAttr?: 0, defStyleRes?: 0)
    }

    private fun bindViews() {
        inflate(context, R.layout.__view_quick_main, this)
        rootChild.generateItemViews()
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

    private fun ViewGroup.generateItemViews()/*: List<__QuickDialogItemView>*/ {
        if (QuickEntry.isEmpty()) {
            emptyView.visibility = VISIBLE
            contents.visibility = GONE
            return
        } else {
            emptyView.visibility = GONE
            contents.visibility = VISIBLE
        }
        val items = QuickEntry.getList()
        //val result = mutableListOf<__QuickDialogItemView>()
        for ((title, impl) in items) {
            if (!impl.shouldShowEntry(context as Activity)) continue
            //val current = __QuickDialogItemView(context)
            val current = inflate(context, R.layout.__item_quick_dialog, null) as __QuickDialogItemView
            current.title = title
            current.content = impl.description()
            current.setOnClickListener { impl.run(context) }
            //result.add(current)
            addView(current)
        }
        //return result
    }
}