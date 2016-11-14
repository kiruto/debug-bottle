package me.chunyu.dev.yuriel.kotdebugtool

import android.app.Application
import android.content.Context
import android.util.Log
import com.exyui.android.debugbottle.components.DTInstaller
import com.squareup.okhttp.OkHttpClient

/**
 * Created by yuriel on 8/9/16.
 */
class DemoApplication : Application() {

    private val TAG = "DemoApplication"

    override fun onCreate() {
        super.onCreate()
        appContext = this

        DTInstaller.install(this)
                .setBlockCanary(AppBlockCanaryContext(this))
                .setOkHttpClient(httpClient)
                .setInjector("me.chunyu.dev.yuriel.kotdebugtool.ContentInjector")
                //.disable()
                .enable()
                .run()

        val h3i = DTInstaller.getOkHttp3Interceptor()
        Log.d(TAG, h3i.toString())
    }

    companion object {
        val httpClient by lazy { OkHttpClient() }

        var appContext: Context? = null
            private set
    }
}
