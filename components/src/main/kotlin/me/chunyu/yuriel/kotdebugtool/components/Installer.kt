package me.chunyu.yuriel.kotdebugtool.components

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import com.squareup.leakcanary.LeakCanary
import me.chunyu.yuriel.kotdebugtool.ui.BlockCanary
import me.chunyu.yuriel.kotdebugtool.ui.BlockCanaryContext

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.GINGERBREAD
import android.os.Bundle
import android.os.Process
import android.os.StrictMode
import android.util.Log
import com.squareup.okhttp.OkHttpClient
import me.chunyu.yuriel.kotdebugtool.components.injector.Injector
import me.chunyu.yuriel.kotdebugtool.components.okhttp.LoggingInterceptor

/**
 * Created by yuriel on 8/10/16.
 */
object Installer: Application.ActivityLifecycleCallbacks {

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
            if (!installed) field = value
        }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        DTActivityManager.topActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }

    override fun onActivityDestroyed(activity: Activity) {

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
            val blockCanary = BlockCanary.install(blockCanary!!)
            if (__DTSettings.getBlockCanaryEnable()) {
                blockCanary.start()
            } else {
                blockCanary.stop()
            }
        }
        if (null != app) {
            if (__DTSettings.getStrictMode()) {
                enableStrictMode()
            }
            if (__DTSettings.getLeakCanaryEnable()) {
                LeakCanary.install(app)
            }
            showNotification(app!!)
            registerActivityLifecycleCallbacks(app!!)
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
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build())
        }
    }

    private fun showNotification(app: Application) {
        //val view = RemoteViews(app.packageName, R.layout.__notification_main)
        //view.setTextViewText(R.id.notify_title, "start")
        val pi = PendingIntent.getActivity(app, 0, Intent(app, __DTDrawerActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val notification: Notification
        val notify = Notification.Builder(app)
                .setSmallIcon(R.drawable.__dt_notification_bt)
                .setTicker("debug tool")
                .setContentIntent(pi)
                .setContentTitle("title")
                .setContentText("test")
                .setOngoing(true)
        if (SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            notification = notify.notification
        } else {
            notification = notify.build()
        }

        val mNotifyMgr = app.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(12030, notification)
        Log.d(javaClass.simpleName, "started")
    }

    private fun registerActivityLifecycleCallbacks(app: Application) {
        app.registerActivityLifecycleCallbacks(this)
    }

    fun kill() {
        Process.killProcess(Process.myPid())
    }

    internal fun getSP(fileName: String): SharedPreferences? {
        return app?.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }
}