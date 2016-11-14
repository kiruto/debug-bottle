package com.exyui.android.debugbottle.components.okhttp

/**
 * Created by yuriel on 11/14/16.
 */

internal const val OK_HTTP_CLIENT_CLASS = "com.squareup.okhttp.OkHttpClient"
internal const val OK_HTTP_CLIENT_3_CLASS = "okhttp3.OkHttpClient"
internal const val F_BREAK = " %n"
internal const val F_URL = " %s"
internal const val F_TIME = " in %.1fms"
internal const val F_HEADERS = "%s"
internal const val F_RESPONSE = F_BREAK + "Response: %d"
internal const val F_BODY = "body: %s"

internal const val F_BREAKER = F_BREAK + "-------------------------------------------" + F_BREAK

internal const val F_REQUEST_WITHOUT_BODY =
        F_URL + F_TIME + F_BREAK +
                F_HEADERS

internal const val F_RESPONSE_WITHOUT_BODY =
        F_RESPONSE + F_BREAK +
                F_HEADERS + F_BREAKER

internal const val F_REQUEST_WITH_BODY =
        F_URL + F_TIME + F_BREAK +
                F_HEADERS + F_BODY + F_BREAK

internal const val F_RESPONSE_WITH_BODY =
        F_RESPONSE + F_BREAK +
                F_HEADERS + F_BODY + F_BREAK +
                F_BREAKER