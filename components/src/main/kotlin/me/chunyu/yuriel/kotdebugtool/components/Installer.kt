package me.chunyu.yuriel.kotdebugtool.components

import android.app.*
import android.content.Intent
import com.squareup.leakcanary.LeakCanary
import me.chunyu.yuriel.kotdebugtool.ui.BlockCanary
import me.chunyu.yuriel.kotdebugtool.ui.BlockCanaryContext

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.GINGERBREAD
import android.os.Process
import android.os.StrictMode
import android.util.Log
import com.squareup.okhttp.OkHttpClient
import me.chunyu.yuriel.kotdebugtool.ui.R
import me.chunyu.yuriel.kotdebugtool.components.injector.Injector
import me.chunyu.yuriel.kotdebugtool.components.okhttp.LoggingInterceptor

/**
 * Created by yuriel on 8/10/16.
 */
object Installer {

    private var installed: Boolean = false
    private var blockCanary: BlockCanaryContext? = null
        set(value) {
            if (!installed) field = value
        }
    private var app: Application? = null
        set(value) {
            if (!installed) field = value
        }
    private var injector: Injector? = null
        set(value) {
            if (!installed) field = value
        }
    internal var rootPackageName: String? = null
        set(value) {
            if (!installed) field = value
        }
    private var httpClient: OkHttpClient? = null
        set(value) {
            if (!installed) field = httpClient
        }

    fun install(app: Application): Installer {
        this.app = app
        return this
    }

    fun setBlockCanary(context: BlockCanaryContext): Installer {
        blockCanary = context
        return this
    }

    fun setInjector(injector: Injector): Installer {
        this.injector = injector
        return this
    }

    fun setInjector(packageName: String): Installer {
        try {
            val injectorClass = Class.forName(packageName)
            injector = injectorClass.newInstance() as Injector
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun setPackageName(name: String): Installer {
        rootPackageName = name
        return this
    }

    fun setOkHttpClient(client: OkHttpClient): Installer {
        httpClient = client
        return this
    }

    fun run() {
        installed = true
        if (null != blockCanary) {
            BlockCanary.install(blockCanary!!).start()
        }
        if (null != app) {
            enableStrictMode()
            LeakCanary.install(app)
            showNotification(app!!)
        }
        if (null != injector) {
            injector?.inject()
        }
        if (null != httpClient) {
            httpClient!!.interceptors().add(LoggingInterceptor())
        }
        app = null
    }

    private fun enableStrictMode() {
        if (SDK_INT >= GINGERBREAD) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build())
        }
    }

    private fun showNotification(app: Application) {
        //val view = RemoteViews(app.packageName, R.layout.__notification_main)
        //view.setTextViewText(R.id.notify_title, "start")
        val pi = PendingIntent.getActivity(app, 0, Intent(app, __TestingActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val notify = Notification.Builder(app)
                .setSmallIcon(R.drawable.__block_canary_notification)
                .setTicker("debug tool")
                .setContentIntent(pi)
                .setContentTitle("title")
                .setContentText("test")
                .setOngoing(true)
                .build()
        val mNotifyMgr = app.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(12030, notify)
        Log.d(javaClass.simpleName, "started")
    }

    fun kill() {
        Process.killProcess(Process.myPid())
    }
}