package com.exyui.android.debugbottle.components.testing

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

/**
 * Created by yuriel on 11/7/16.
 *
 * Monkey test black list implement class
 */
internal class MonkeyBlackList private constructor (context: Context) {
    val blackList: MutableSet<String> = mutableSetOf()
    private val app = context.applicationContext

    companion object {
        private var instance: MonkeyBlackList? = null
        fun getInstance(context: Context): MonkeyBlackList {
            if (null == instance) {
                instance = MonkeyBlackList(context)
            }
            return instance!!
        }
    }

    internal fun exclude(cls: Class<out Activity>): MonkeyBlackList {
        return exclude(cls.name)
    }

    /**
     * @param className Activity's class name. Can use "package.*" to exclude multiple activities.
     */
    internal fun exclude(className: String): MonkeyBlackList {
        if (className.endsWith(".*")) {
            val mgr = app.packageManager
            val packageName = app.packageName
            val info: PackageInfo = mgr.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            val list = info.activities
            for (a in list?: arrayOf<ActivityInfo>()) {
                if (a.name.startsWith(className.replace(".*", ""))) {
                    blackList.add(a.name)
                }
            }
        } else {
            blackList.add(className)
        }
        return this
    }

    internal fun contains(className: String): Boolean = blackList.contains(className)

    internal fun contains(cls: Class<out Activity>) = blackList.contains(cls.name)
}