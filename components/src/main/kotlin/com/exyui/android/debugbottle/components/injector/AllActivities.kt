package com.exyui.android.debugbottle.components.injector

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.exyui.android.debugbottle.components.DTInstaller
import com.exyui.android.debugbottle.components.injector.__IntentInjectorImpl

/**
 * Created by yuriel on 8/15/16.
 */
internal class AllActivities(activity: Activity): __IntentInjectorImpl() {
    init {
        setActivity(activity)
        val mgr = activity.packageManager
        val packageName = activity.application.packageName
        val info: PackageInfo = mgr.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        val list = info.activities
        for (a in list) {
            val intent = Intent()
            intent.setClassName(a.packageName, a.name)
            put(a.name.split(".").last(), intent)
        }
    }
}