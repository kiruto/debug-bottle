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

    private fun getSP() = DTInstaller.getSP(DT_SETTING_STORE_FILE)

    /**
     * return if user enabled debug bottle
     *
     * Changes will effect after application restarts.
     */
    var bottleEnable: Boolean
        set(value) {
            getSP()?.edit()?.putBoolean(BOTTLE_ENABLE, value)?.apply()
            DTInstaller.setNotificationDisplay(value)
        }
        get() = getSP()?.getBoolean(BOTTLE_ENABLE, false)?: false

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
     *
     * Changes will effect after application restarts.
     */
    var strictMode: Boolean
        set(value) {
            getSP()?.edit()?.putBoolean(STRICT_MODE, value)?.apply()
        }
        get() = getSP()?.getBoolean(STRICT_MODE, false)?: false


    /**
     * return if user enabled leak canary
     *
     * Changes will effect after application restarts.
     */
    var leakCanaryEnable: Boolean
        set(value) {
            getSP()?.edit()?.putBoolean(LEAK_CANARY_ENABLE, value)?.apply()
        }
        get() = getSP()?.getBoolean(LEAK_CANARY_ENABLE, false)?: false

    /**
     * return if user enabled block canary
     *
     * Changes will effect after application restarts.
     */
    var blockCanaryEnable: Boolean
        set(value) {
            getSP()?.edit()?.putBoolean(BLOCK_CANARY_ENABLE, value)?.apply()
        }
        get() = getSP()?.getBoolean(BLOCK_CANARY_ENABLE, false)?: false

    /**
     * the threshold(ms) to confirm if ui has blocked
     */
    var blockThreshold: Long
        set(value) {
            getSP()?.edit()?.putLong(BLOCK_THRESHOLD, value)?.apply()
        }
        get() = getSP()?.getLong(BLOCK_THRESHOLD, DEFAULT_BLOCK_THRESHOLD)?: DEFAULT_BLOCK_THRESHOLD

    /**
     * Seed value for pseudo-random number generator.
     * If you re-run the stress test with the same seed value, it will generate the same sequence of events.
     */
    var testingSeed: Int
        set(value) {
            getSP()?.edit()?.putInt(TESTING_SEED, value)?.apply()
        }
        get() = getSP()?.getInt(TESTING_SEED, DEFAULT_TESTING_SEED)?: DEFAULT_TESTING_SEED

    var testingSendKeyEvent: Boolean
        set(value) {
            getSP()?.edit()?.putBoolean(TESTING_SEND_KEY_EVENT, value)?.apply()
        }
        get() = getSP()?.getBoolean(TESTING_SEND_KEY_EVENT, false)?: false

    /**
     * Events will be sent
     */
    var testingEventCount: Int
        set(value) {
            getSP()?.edit()?.putInt(TESTING_EVENT_COUNT, value)?.apply()
        }
        get() = getSP()?.getInt(TESTING_EVENT_COUNT, DEFAULT_TESTING_EVENT_COUNT)?: DEFAULT_TESTING_EVENT_COUNT

    /**
     * verbosity level
     */
    var testingLogDepth: Int
        set(value) {
            getSP()?.edit()?.putInt(TESTING_LOG_DEPTH, value)?.apply()
        }
        get() = getSP()?.getInt(TESTING_LOG_DEPTH, 1)?: 1

    /**
     * Display FPS on screen
     */
    var frameEnable: Boolean
        set(value) {
            __FrameRecorder.enabled = value
        }
        get() = __FrameRecorder.enabled
}