package me.chunyu.yuriel.kotdebugtool.components.injector

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import me.chunyu.yuriel.kotdebugtool.components.Installer
import me.chunyu.yuriel.kotdebugtool.components.injector.IntentInjectorImpl

/**
 * Created by yuriel on 8/15/16.
 */
internal class AllActivities(activity: Activity): IntentInjectorImpl() {
    init {
        setActivity(activity)
        try {
            val mgr = activity.packageManager
            val packageName = Installer.rootPackageName?: activity.packageName
            val info = mgr.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            //val test = info.applicationInfo
            val list = info.activities
            for (a in list) {
                val clazz = Class.forName(a.name)
                add(clazz.simpleName, Intent(activity, clazz))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}