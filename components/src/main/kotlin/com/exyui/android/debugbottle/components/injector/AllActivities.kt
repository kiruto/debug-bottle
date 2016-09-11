package com.exyui.android.debugbottle.components.injector

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import com.exyui.android.debugbottle.components.DTInstaller
import com.exyui.android.debugbottle.components.injector.__IntentInjectorImpl

/**
 * Created by yuriel on 8/15/16.
 */
internal class AllActivities(activity: Activity): __IntentInjectorImpl() {
    init {
        setActivity(activity)
        try {
            val mgr = activity.packageManager
            val packageName = DTInstaller.rootPackageName?: activity.packageName
            val info = mgr.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            //val test = info.applicationInfo
            val list = info.activities
            for (a in list) {
                val clazz = Class.forName(a.name)
                put(clazz.simpleName, Intent(activity, clazz))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}