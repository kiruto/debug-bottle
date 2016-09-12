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
//            for (info in getActivities(activity.applicationContext)) {
//                val content = getIntent2(info)
//                put (content.first, content.second)
//            }
    }

//    private fun getActivities(context: Context): List<ActivityInfo> {
//        val result = mutableListOf<ActivityInfo>()
//        val intent = Intent(Intent.ACTION_MAIN, null)
//        intent.`package` = context.packageName
//        for (info in context.packageManager.queryIntentActivities(intent, 0)) {
//            result.add(info.activityInfo)
//        }
//        return result
//    }
//
//    private fun getIntent(info: ActivityInfo): Pair<String, Intent> {
//        val name = info.name.split(".").last()
//        val componentName = ComponentName(info.applicationInfo.packageName, info.name)
//        val result = Intent(Intent.ACTION_MAIN)
//        result.addCategory(Intent.CATEGORY_LAUNCHER)
//        result.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
//        result.component = componentName
//        return Pair(name, result)
//    }
//
//    private fun getIntent2(info: ActivityInfo): Pair<String, Intent> {
//        val name = info.name.split(".").last()
//        val intent = Intent()
//        intent.setClassName(info.applicationInfo.packageName, info.name)
//        return Pair(name, intent)
//    }
}