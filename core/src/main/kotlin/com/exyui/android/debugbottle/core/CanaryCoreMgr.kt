package com.exyui.android.debugbottle.core

import com.exyui.android.debugbottle.core.CanaryCore
import com.exyui.android.debugbottle.core.IBlockCanaryContext

/**
 * Created by yuriel on 8/10/16.
 */
object CanaryCoreMgr {
    var context: IBlockCanaryContext? = null
        set(value) {
            if (null == context) field = value
        }

    /**
     * Get CanaryCore singleton

     * @return CanaryCore instance
     */
    fun get(): CanaryCore = CanaryCore
}