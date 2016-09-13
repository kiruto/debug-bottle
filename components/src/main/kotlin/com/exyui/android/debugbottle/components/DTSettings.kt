package com.exyui.android.debugbottle.components

import android.content.SharedPreferences
import com.exyui.android.debugbottle.core.*

/**
 * Created by yuriel on 8/22/16.
 */
internal object DTSettings {

    private fun getSP() = DTInstaller.getSP(DT_SETTING_STORE_FILE)

    fun setBottleEnable(enable: Boolean) {
        getSP()?.edit()?.putBoolean(BOTTLE_ENABLE, enable)?.apply()
        DTInstaller.setNotificationDisplay(enable)
    }

    fun getBottleEnable() = getSP()?.getBoolean(BOTTLE_ENABLE, false)?: false

    fun setBlockThreshold(threshold: Long) {
        getSP()?.edit()?.putLong(BLOCK_THRESHOLD, threshold)?.apply()
    }

    fun getBlockThreshold() = getSP()?.getLong(BLOCK_THRESHOLD, DEFAULT_BLOCK_THRESHOLD)?: DEFAULT_BLOCK_THRESHOLD

    fun getHttpFileStorePath() = "/ktdebugtools/http"

    fun setNetworkSniff(sniff: Boolean) {
        getSP()?.edit()?.putBoolean(NETWORK_SNIFF, sniff)?.apply()
        if (sniff) {
            RunningFeatureMgr.add(RunningFeatureMgr.NETWORK_LISTENER)
        } else {
            RunningFeatureMgr.remove(RunningFeatureMgr.NETWORK_LISTENER)
        }
    }

    fun getNetworkSniff(): Boolean {
        val result = getSP()?.getBoolean(NETWORK_SNIFF, false)?: false
        if (result) {
            RunningFeatureMgr.add(RunningFeatureMgr.NETWORK_LISTENER)
        } else {
            RunningFeatureMgr.remove(RunningFeatureMgr.NETWORK_LISTENER)
        }
        return result
    }

    fun getCrashFileStorePath() = "/ktdebugtools/crash"

    fun setStrictMode(strictMode: Boolean) {
        getSP()?.edit()?.putBoolean(STRICT_MODE, strictMode)?.apply()
    }

    fun getStrictMode() = getSP()?.getBoolean(STRICT_MODE, false)?: false

    fun setLeakCanaryEnable(enable: Boolean) {
        getSP()?.edit()?.putBoolean(LEAK_CANARY_ENABLE, enable)?.apply()
    }

    fun getLeakCanaryEnable() = getSP()?.getBoolean(LEAK_CANARY_ENABLE, false)?: false

    fun setBlockCanaryEnable(enable: Boolean) {
        getSP()?.edit()?.putBoolean(BLOCK_CANARY_ENABLE, enable)?.apply()
    }

    fun getBlockCanaryEnable() = getSP()?.getBoolean(BLOCK_CANARY_ENABLE, false)?: false
}