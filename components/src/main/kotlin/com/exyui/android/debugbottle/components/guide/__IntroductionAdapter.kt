package com.exyui.android.debugbottle.components.guide

import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by yuriel on 9/10/16.
 */
internal class __IntroductionAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val color = when(position) {
            0 -> "#607D8B"
            1 -> "#673AB7"
            2 -> "#1565C0"
            3 -> "#009688"
            else -> throw NotImplementedError()
        }
        val colorInt = Color.parseColor(color)

        return __IntroductionFragment.newInstance(colorInt, position)
    }

    override fun getCount(): Int = 4
}