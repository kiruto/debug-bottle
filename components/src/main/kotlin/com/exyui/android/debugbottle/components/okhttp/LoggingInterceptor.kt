package com.exyui.android.debugbottle.components.okhttp

import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import com.squareup.okhttp.ResponseBody
import com.exyui.android.debugbottle.components.DTSettings

import java.io.IOException

import okio.Buffer

/**
 * Created by yuriel on 8/18/16.
 */
internal class LoggingInterceptor : Interceptor {

    @Throws(IOException::class)
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
                println(String.format("GET ${F_REQUEST_WITHOUT_BODY + F_RESPONSE_WITH_BODY}",
                        request.url(), time, request.headers(),
                        response.code(), response.headers(), responseBody))

            }
            "POST" -> {
                requestBody = stringifyRequestBody(request)
                responseBody = stringifyResponseBody(bodyString!!)
                println(String.format("POST ${F_REQUEST_WITH_BODY + F_RESPONSE_WITH_BODY}",
                        request.url(), time, request.headers(), requestBody,
                        response.code(), response.headers(), responseBody))
            }
            "PUT" -> {
                requestBody = request.body().toString()
                responseBody = stringifyResponseBody(bodyString!!)
                println(String.format("PUT ${F_REQUEST_WITH_BODY + F_RESPONSE_WITH_BODY}",
                        request.url(), time, request.headers(), requestBody,
                        response.code(), response.headers(), responseBody))
            }
            "DELETE" -> {
                println(String.format("DELETE ${F_REQUEST_WITHOUT_BODY + F_RESPONSE_WITHOUT_BODY}",
                        request.url(), time, request.headers(),
                        response.code(), response.headers()))
            }
        }

        val block = HttpBlock.newInstance(request, response, t1, t2, requestBody, responseBody)
        if (DTSettings.getNetworkSniff()) {
            HttpBlockFileMgr.saveHttpLog(block.toString())
        }
        if (response.body() != null) {
            val body = ResponseBody.create(contentType, bodyString)
            return response.newBuilder().body(body).build()
        } else {
            return response
        }
    }

    fun stringifyResponseBody(responseBody: String): String {
        return responseBody
    }

    companion object {

        private val F_BREAK = " %n"
        private val F_URL = " %s"
        private val F_TIME = " in %.1fms"
        private val F_HEADERS = "%s"
        private val F_RESPONSE = F_BREAK + "Response: %d"
        private val F_BODY = "body: %s"

        private val F_BREAKER = F_BREAK + "-------------------------------------------" + F_BREAK

        private val F_REQUEST_WITHOUT_BODY =
                F_URL + F_TIME + F_BREAK +
                F_HEADERS

        private val F_RESPONSE_WITHOUT_BODY = F_RESPONSE + F_BREAK +
                F_HEADERS + F_BREAKER

        private val F_REQUEST_WITH_BODY =
                F_URL + F_TIME + F_BREAK +
                F_HEADERS + F_BODY + F_BREAK

        private val F_RESPONSE_WITH_BODY =
                F_RESPONSE + F_BREAK +
                F_HEADERS + F_BODY + F_BREAK +
                F_BREAKER


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
    }
}