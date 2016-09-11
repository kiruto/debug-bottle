package com.exyui.android.debugbottle.core.log

import android.app.ActivityManager
import android.content.Context
import com.exyui.android.debugbottle.core.__CanaryCore
import com.exyui.android.debugbottle.core.__CanaryCoreMgr

/**
 * Created by yuriel on 8/8/16.
 */
object __ProcessUtils {

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
            sProcessName = obtainProcessName(__CanaryCoreMgr.context?.context!!)
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
