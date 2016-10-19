package com.exyui.android.debugbottle.components.injector

import android.app.Activity
import android.content.Context
import com.exyui.android.debugbottle.components.DTActivityManager
import java.util.*

/**
 * Created by yuriel on 10/14/16.
 */
object QuickEntry {

    private val model: LinkedHashMap<String, OnActivityDisplayedListener> = LinkedHashMap()

    fun put(clazz: Class<out Activity>, name: String, t: Runnable, description: String? = null) {
        val l = object: OnActivityDisplayedListener {
            override fun shouldShowEntry(activity: Activity?): Boolean = activity?.javaClass == clazz.javaClass

            override fun run(context: Context?) {
                t.run()
            }

            override fun description(): String? = description
        }
        put(name, l)
    }

    fun put(name: String, listener: OnActivityDisplayedListener) {
        model.put(name, listener)
    }

    operator fun get(name: String): OnActivityDisplayedListener? = model[name]

    fun run(t: OnActivityDisplayedListener?) {
        t?.run(DTActivityManager.topActivity)
    }

    fun run(name: String) {
        run(this[name])
    }

    internal fun getList(): LinkedHashMap<String, OnActivityDisplayedListener> {
        val result = LinkedHashMap<String, OnActivityDisplayedListener>()
        for ((k, m) in model) {
            if (m.shouldShowEntry(DTActivityManager.topActivity)) {
                result.put(k, m)
            }
        }
        return result
    }

    internal fun isEmpty() = model.isEmpty()

    interface OnActivityDisplayedListener {
        fun shouldShowEntry(activity: Activity?): Boolean
        fun run(context: Context?)
        fun description(): String?
    }
}