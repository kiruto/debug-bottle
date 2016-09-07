package me.chunyu.dev.yuriel.kotdebugtool

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.exyui.android.debugbottle.ui.BlockCanaryContext

/**
 * Created by yuriel on 8/9/16.
 */
class AppBlockCanaryContext(context: Context) : BlockCanaryContext(context) {

    private val TAG = "AppBlockCanaryContext"

    /**
     * 标示符，可以唯一标示该安装版本号，如版本+渠道名+编译平台

     * @return apk唯一标示符
     */
    override val qualifier: String
        get() {
            var qualifier = ""
            try {
                val appContext = DemoApplication.appContext
                val info = appContext?.packageManager?.getPackageInfo(appContext.packageName, 0)
                qualifier += "${info?.versionCode}_${info?.versionName}_YYB"
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, "getQualifier exception", e)
            }

            return qualifier
        }

    override val uid: String = "87224330"

    override val networkType: String = "4G"

    override val configDuration: Int = 9999

    override val isNeedDisplay: Boolean = BuildConfig.DEBUG

    override val logPath: String = "/ktdebugtools/blocks"
}