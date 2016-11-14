package com.exyui.android.debugbottle.components.okhttp

/**
 * Created by yuriel on 11/14/16.
 */

internal object OkHttpLoader {
    fun load(client: Any?): Boolean {
        client?.let {
            if (it.javaClass.name == OK_HTTP_CLIENT_CLASS) {
                val c = it as com.squareup.okhttp.OkHttpClient
                c.interceptors().add(LoggingInterceptor())
                return true
            }
        }
        return false
    }
}