package me.chunyu.dev.yuriel.kotdebugtool

import android.app.Application
import android.content.Context
import me.chunyu.yuriel.kotdebugtool.components.Installer

/**
 * Created by yuriel on 8/9/16.
 */
class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        Installer.install(this)
                .setBlockCanary(AppBlockCanaryContext(this))
                //.setPackageName("me.chunyu")
                .run()
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}
