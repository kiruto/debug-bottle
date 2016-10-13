package com.exyui.android.debugbottle.components.dialog.quickfragments

import android.annotation.TargetApi
import android.app.Fragment
import android.app.FragmentManager
import android.os.Build
import android.support.annotation.IdRes

/**
 * Created by yuriel on 10/13/16.
 */
abstract class __BaseQuickDialogFragment: Fragment() {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected fun fm(): FragmentManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            childFragmentManager
        else
            fragmentManager
    }
}