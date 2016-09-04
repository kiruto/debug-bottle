package me.chunyu.dev.yuriel.kotdebugtool

import android.app.Application
import android.content.Context
import android.content.Intent
import com.squareup.okhttp.OkHttpClient
import me.chunyu.yuriel.kotdebugtool.components.Installer
import me.chunyu.yuriel.kotdebugtool.components.injector.IntentInjector

/**
 * Created by yuriel on 8/9/16.
 */
class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        IntentInjector.put("activity entry", Intent(this, DemoActivity::class.java))

        Installer.install(this)
                .setBlockCanary(AppBlockCanaryContext(this))
                .setOkHttpClient(httpClient)
                //.setPackageName("me.chunyu")
                .run()
    }

    companion object {
        val httpClient by lazy { OkHttpClient() }
        var appContext: Context? = null
            private set
    }
}
