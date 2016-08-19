package me.chunyu.yuriel.kotdebugtool.core.log

import android.app.ActivityManager
import android.content.Context
import me.chunyu.yuriel.kotdebugtool.core.CanaryCore
import me.chunyu.yuriel.kotdebugtool.core.CanaryCoreMgr

/**
 * Created by yuriel on 8/8/16.
 */
object ProcessUtils {

    @Volatile private var sProcessName: String? = null
    private val sNameLock = Any()

    fun myProcessName(): String {
        if (sProcessName?.isNotEmpty()?: false) {
            return sProcessName?: ""
        }
        synchronized(sNameLock) {
            if (sProcessName?.isNotEmpty()?: false) {
                return sProcessName?: ""
            }
            sProcessName = obtainProcessName(CanaryCoreMgr.context?.context!!)
            return sProcessName?: ""
        }
    }

    private fun obtainProcessName(context: Context): String? {
        val pid = android.os.Process.myPid()
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val listTaskInfo = am.runningAppProcesses
        if (listTaskInfo != null && !listTaskInfo.isEmpty()) {
            for (info in listTaskInfo) {
                if (info != null && info.pid == pid) {
                    return info.processName
                }
            }
        }
        return null
    }
}
