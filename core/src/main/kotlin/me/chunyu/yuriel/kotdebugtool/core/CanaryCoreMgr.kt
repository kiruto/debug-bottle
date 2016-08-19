package me.chunyu.yuriel.kotdebugtool.core

import me.chunyu.yuriel.kotdebugtool.core.CanaryCore
import me.chunyu.yuriel.kotdebugtool.core.IBlockCanaryContext

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