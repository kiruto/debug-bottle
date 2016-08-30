package me.chunyu.yuriel.kotdebugtool.components

import android.content.SharedPreferences
import me.chunyu.yuriel.kotdebugtool.core.*

/**
 * Created by yuriel on 8/22/16.
 */
internal object __DTSettings {

    private fun getSP() = Installer.getSP(DT_SETTING_STORE_FILE)

    fun setBlockThreshold(threshold: Long) {
        getSP()?.edit()?.putLong(BLOCK_THRESHOLD, threshold)?.apply()
    }

    fun getBlockThreshold() = getSP()?.getLong(BLOCK_THRESHOLD, DEFAULT_BLOCK_THRESHOLD)?: DEFAULT_BLOCK_THRESHOLD

    fun getHttpFileStorPath() = "/ktdebugtools/http"

    fun setNetworkSniff(sniff: Boolean) {
        getSP()?.edit()?.putBoolean(NETWORK_SNIFF, sniff)?.apply()
    }

    fun getNetworkSniff() = getSP()?.getBoolean(NETWORK_SNIFF, false)?: false

    fun setStrictMode(strictMode: Boolean) {
        getSP()?.edit()?.putBoolean(STRICT_MODE, strictMode)?.apply()
    }

    fun getStrictMode() = getSP()?.getBoolean(STRICT_MODE, false)?: false
}