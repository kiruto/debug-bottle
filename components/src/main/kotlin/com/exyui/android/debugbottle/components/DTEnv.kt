package com.exyui.android.debugbottle.components

/**
 * Created by yuriel on 11/11/16.
 */
@Suppress("unused")
object DTEnv {

    @JvmStatic val ENV = Env.DEBUG
    @JvmStatic val VERSION_CODE = BuildConfig.VERSION_CODE
    @JvmStatic val VERSION_NAME = BuildConfig.VERSION_NAME

    @JvmStatic fun isDebug() = ENV == Env.DEBUG
    @JvmStatic fun isRelease() = ENV == Env.RELEASE
    @JvmStatic fun isTest() = ENV == Env.TEST

    enum class Env {
        DEBUG, RELEASE, TEST
    }
}