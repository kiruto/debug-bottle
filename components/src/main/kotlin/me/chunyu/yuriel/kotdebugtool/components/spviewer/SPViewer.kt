package me.chunyu.yuriel.kotdebugtool.components.spviewer

import android.content.Context
import android.content.SharedPreferences
import java.io.File
import java.util.*

/**
 * Created by yuriel on 8/16/16.
 */
internal class SPViewer(private val appContext: Context) {
    private fun getSPFiles(): Array<out String> {
        val prefsdir = File(appContext.applicationInfo.dataDir, "shared_prefs")
        if (prefsdir.exists() && prefsdir.isDirectory) {
            return prefsdir.list()
        } else {
            return arrayOf()
        }
    }

    fun getAll(): LinkedHashMap<String, SharedPreferences> {
        val list = getSPFiles()
        val result = LinkedHashMap<String, SharedPreferences>()
        for (f in list) {
            val preffile = f.substring(0, f.length - 4)
            result.put(f, appContext.getSharedPreferences(preffile, Context.MODE_PRIVATE))
        }
        return result
    }
}