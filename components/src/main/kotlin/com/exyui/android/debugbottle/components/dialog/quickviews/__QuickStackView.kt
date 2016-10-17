package com.exyui.android.debugbottle.components.dialog.quickviews

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.Log
import android.widget.ScrollView
import com.exyui.android.debugbottle.components.R
import java.util.*

/**
 * Created by yuriel on 10/14/16.
 */
class __QuickStackView: ScrollView {

    private val TAG = "__QuickStackView"

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
    }

    private fun bindViews() {
        inflate(context, R.layout.__view_quick_stack, this)

    }

    private fun listActivities() {
        if (context is AppCompatActivity) {
            val activity = context as AppCompatActivity
            val fragments = activity.supportFragmentManager.fragments

        }
    }
}