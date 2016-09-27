package com.exyui.android.debugbottle.components

import android.content.SharedPreferences
import com.exyui.android.debugbottle.core.*

/**
 * Created by yuriel on 8/22/16.
 */
internal object DTSettings {

    val GITHUB_URL = "https://github.com/kiruto/debug-bottle"
    var httpFileStorePath = "/ktdebugtools/http"
    var crashFileStorePath = "/ktdebugtools/crash"

    /**
     * return if user enabled debug bottle
     */
    var bottleEnable: Boolean
        set(value) {
            getSP()?.edit()?.putBoolean(BOTTLE_ENABLE, value)?.apply()
            DTInstaller.setNotificationDisplay(value)
        }
        get() = getSP()?.getBoolean(BOTTLE_ENABLE, false)?: false

    /**
     * the threshold(ms) to confirm if ui has blocked
     */
    var blockThreshold: Long
        set(value) {
            getSP()?.edit()?.putLong(BLOCK_THRESHOLD, value)?.apply()
        }
        get() = getSP()?.getLong(BLOCK_THRESHOLD, DEFAULT_BLOCK_THRESHOLD)?: DEFAULT_BLOCK_THRESHOLD

    /**
     * return if user enabled network sniffer
     */
    var networkSniff: Boolean
        set(value) {
            getSP()?.edit()?.putBoolean(NETWORK_SNIFF, value)?.apply()
            if (value) {
                RunningFeatureMgr.add(RunningFeatureMgr.NETWORK_LISTENER)
            } else {
                RunningFeatureMgr.remove(RunningFeatureMgr.NETWORK_LISTENER)
            }
        }
        get() {
            val result = getSP()?.getBoolean(NETWORK_SNIFF, false)?: false
            if (result) {
                RunningFeatureMgr.add(RunningFeatureMgr.NETWORK_LISTENER)
            } else {
                RunningFeatureMgr.remove(RunningFeatureMgr.NETWORK_LISTENER)
            }
            return result
        }

    /**
     * return if user enabled strict mode
     */
    var strictMode: Boolean
        set(value) {
            getSP()?.edit()?.putBoolean(STRICT_MODE, value)?.apply()
        }
        get() = getSP()?.getBoolean(STRICT_MODE, false)?: false


    /**
     * return if user enabled leak canary
     */
    var leakCanaryEnable: Boolean
        set(value) {
            getSP()?.edit()?.putBoolean(LEAK_CANARY_ENABLE, value)?.apply()
        }
        get() = getSP()?.getBoolean(LEAK_CANARY_ENABLE, false)?: false

    /**
     * return if user enabled block canary
     */
    var blockCanaryEnable: Boolean
        set(value) {
            getSP()?.edit()?.putBoolean(BLOCK_CANARY_ENABLE, value)?.apply()
        }
        get() = getSP()?.getBoolean(BLOCK_CANARY_ENABLE, false)?: false

    private fun getSP() = DTInstaller.getSP(DT_SETTING_STORE_FILE)
}