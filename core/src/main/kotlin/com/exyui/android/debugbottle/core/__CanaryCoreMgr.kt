package com.exyui.android.debugbottle.core

import com.exyui.android.debugbottle.core.__CanaryCore
import com.exyui.android.debugbottle.core.__IBlockCanaryContext

/**
 * Created by yuriel on 8/10/16.
 */
object __CanaryCoreMgr {
    var context: __IBlockCanaryContext? = null
        set(value) {
            if (null == context) field = value
        }

    /**
     * Get __CanaryCore singleton

     * @return __CanaryCore instance
     */
    fun get(): __CanaryCore = __CanaryCore
}