package com.exyui.android.debugbottle.components.okhttp

import android.util.Log
import com.exyui.android.debugbottle.components.DTSettings
import okhttp3.*
import okio.Buffer
import java.io.IOException

/**
 * Created by yuriel on 11/14/16.
 */
internal class LoggingInterceptor3 : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val t1 = System.nanoTime()
        val response = chain.proceed(request)
        val t2 = System.nanoTime()

        var contentType: MediaType? = null
        var bodyString: String? = null
        if (response.body() != null) {
            contentType = response.body().contentType()
            bodyString = response.body().string()
        }

        var requestBody = ""
        var responseBody = ""
        val time: Double = (t2 - t1) / 1e6

        when(request.method()) {
            "GET" -> {
                responseBody = stringifyResponseBody(bodyString!!)
                println(String.format("GET $F_REQUEST_WITHOUT_BODY$F_RESPONSE_WITH_BODY",
                        request.url(), time, request.headers(),
                        response.code(), response.headers(), responseBody))
            }
            "POST" -> {
                requestBody = stringifyRequestBody(request)
                responseBody = stringifyResponseBody(bodyString!!)
                println(String.format("POST $F_REQUEST_WITH_BODY$F_RESPONSE_WITH_BODY",
                        request.url(), time, request.headers(), requestBody,
                        response.code(), response.headers(), responseBody))
            }
            "PUT" -> {
                requestBody = request.body().toString()
                responseBody = stringifyResponseBody(bodyString!!)
                println(String.format("PUT $F_REQUEST_WITH_BODY$F_RESPONSE_WITH_BODY",
                        request.url(), time, request.headers(), requestBody,
                        response.code(), response.headers(), responseBody))
            }
            "DELETE" -> {
                println(String.format("DELETE $F_REQUEST_WITHOUT_BODY$F_RESPONSE_WITHOUT_BODY",
                        request.url(), time, request.headers(),
                        response.code(), response.headers()))
            }
        }

        val block = HttpBlock.newInstance(
                url = request.url().toString(),
                method = request.method(),
                timeStart = t1,
                timeEnd = t2,
                requestHeader = request.headers().toString(),
                responseCode = response.code().toString(),
                responseHeader = response.headers().toString(),
                requestBody = requestBody,
                responseBody = responseBody
        )
        if (DTSettings.networkSniff) {
            HttpBlockFileMgr.saveLog(block.toString())
        }
        if (response.body() != null) {
            val body = ResponseBody.create(contentType, bodyString)
            return response.newBuilder().body(body).build()
        } else {
            return response
        }
    }

    private fun stringifyResponseBody(responseBody: String): String {
        return responseBody
    }

    private fun stringifyRequestBody(request: Request): String {
        try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body().writeTo(buffer)
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }
    }

    private fun println(line: String) {
        Log.d("DEBUG-BOTTLE", line)
    }
}