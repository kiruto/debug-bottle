package com.exyui.android.debugbottle.components;

import com.exyui.android.debugbottle.BuildConfig;

/**
 * Created by yuriel on 11/11/16.
 */

@SuppressWarnings("unused")
final public class DTEnv {
    public static final Env ENV = Env.RELEASE;
    public static final int VERSION_CODE = BuildConfig.VERSION_CODE;
    public static final String VERSION_NAME = BuildConfig.VERSION_NAME;

    public static Boolean isDebug() {
        return ENV == Env.DEBUG;
    }

    public static Boolean isRelease() {
        return ENV == Env.RELEASE;
    }

    public static Boolean isTest() {
        return ENV == Env.TEST;
    }

    public enum Env {
        DEBUG, RELEASE, TEST
    }
}
