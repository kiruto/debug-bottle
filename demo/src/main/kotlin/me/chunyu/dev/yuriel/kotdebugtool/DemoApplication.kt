package me.chunyu.dev.yuriel.kotdebugtool

import android.app.Application
import android.content.Context
import com.squareup.okhttp.OkHttpClient
import com.exyui.android.debugbottle.components.DTInstaller

/**
 * Created by yuriel on 8/9/16.
 */
class DemoApplication : Application() {

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
    }

    companion object {
        val httpClient by lazy { OkHttpClient() }
        var appContext: Context? = null
            private set
    }
}
