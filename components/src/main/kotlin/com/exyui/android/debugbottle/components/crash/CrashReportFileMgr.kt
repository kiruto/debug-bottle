package com.exyui.android.debugbottle.components.crash

import com.exyui.android.debugbottle.components.DTReportMgr
import com.exyui.android.debugbottle.components.DTSettings

/**
 * Created by yuriel on 9/13/16.
 */
internal object CrashReportFileMgr : DTReportMgr() {
    override val TAG: String = "CrashReportFileMgr"
    override val logPath: String = DTSettings.getCrashFileStorePath()
    override val filePrefix: String = "crash"
}