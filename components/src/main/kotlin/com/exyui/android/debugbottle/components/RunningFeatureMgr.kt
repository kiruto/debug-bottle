package com.exyui.android.debugbottle.components

import android.util.Log
import kotlin.properties.Delegates

/**
 * Created by yuriel on 9/4/16.
 */
internal object RunningFeatureMgr {
    val DEBUG_BOTTLE = 1 shl 0
    val NETWORK_LISTENER = 1 shl 1
    val STRICT_MODE = 1 shl 2
    val VIEW_3D_WINDOW = 1 shl 3
    val LEAK_CANARY = 1 shl 4
    val BLOCK_CANARY = 1 shl 5
    val CRASH_HANDELER = 1 shl 6
    val MONKEY_TEST_RUNNER = 1 shl 7

    private var flags by Delegates.observable(0) { prop, old, new ->
        Log.d("RunningFeatureMgr", "old: $old, new: $new")
    }

    fun clear() {
        flags = 0
    }

    fun add(flag: Int) {
        flags = (flags or flag)
    }

    fun remove(flag: Int) {
        flags = (flags and flag.inv())
    }

    fun has(flag: Int) = (flags and flag) == flag

}