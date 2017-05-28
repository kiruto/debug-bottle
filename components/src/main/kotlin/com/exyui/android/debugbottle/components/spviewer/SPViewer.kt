package com.exyui.android.debugbottle.components.spviewer

import android.content.Context
import android.content.SharedPreferences
import java.io.File
import java.util.*

/**
 * Created by yuriel on 8/16/16.
 */
internal class SPViewer(private val appContext: Context) {
    private fun getSPFiles(): Array<out String> {
        return File(appContext.applicationInfo.dataDir, "shared_prefs").let {
            if (it.exists() && it.isDirectory) {
                it.list()
            } else {
                arrayOf()
            }
        }
    }

    fun getAll(): LinkedHashMap<String, SharedPreferences> {
        return (LinkedHashMap<String, SharedPreferences>()).apply {
            getSPFiles().forEach {
                put(it, appContext.getSharedPreferences(it.substring(0, it.length - 4), Context.MODE_PRIVATE))
            }
        }
    }
}