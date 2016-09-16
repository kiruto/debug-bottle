package com.exyui.android.debugbottle.components.okhttp

import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.exyui.android.debugbottle.components.DTReportMgr
import com.exyui.android.debugbottle.components.DTSettings
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuriel on 8/24/16.
 */
internal object HttpBlockFileMgr: DTReportMgr() {
    override val TAG = "HttpBlockFileMgr"
    override val logPath = DTSettings.httpFileStorePath
    override val filePrefix = "http"
}