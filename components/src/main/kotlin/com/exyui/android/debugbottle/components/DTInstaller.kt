package com.exyui.android.debugbottle.components

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import com.squareup.leakcanary.LeakCanary
import com.exyui.android.debugbottle.ui.BlockCanary
import com.exyui.android.debugbottle.ui.BlockCanaryContext

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.GINGERBREAD
import android.os.Bundle
import android.os.Process
import android.os.StrictMode
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.util.Log
import android.widget.Toast
import com.exyui.android.debugbottle.components.crash.DTCrashHandler
import com.exyui.android.debugbottle.components.injector.Injector
import com.exyui.android.debugbottle.components.okhttp.LoggingInterceptor3
import com.exyui.android.debugbottle.components.okhttp.OK_HTTP_CLIENT_3_CLASS
import com.exyui.android.debugbottle.components.okhttp.OkHttpLoader
import com.exyui.android.debugbottle.components.testing.MonkeyExcludeActivities

/**
 * Created by yuriel on 8/10/16.
 * Context class of debug bottle, also entry of debug bottle.
 */
@Suppress("unused")
object DTInstaller : Application.ActivityLifecycleCallbacks {

    private var installed: Boolean = false
    private var enabled = true
    //private var notification: Notification? = null
    private val NOTIFICATION_ID = 12030
    @DrawableRes private var notificationIconRes: Int? = null
    private var notificationTitle: String? = null
    private var notificationMessage: String? = null
    private var blockCanary: BlockCanaryContext? = null
        set(value) {
            if (!installed) field = value
        }

    private var injector: Injector? = null
        set(value) {
            if (!installed) field = value
        }

    private var httpClient: Any? = null
        set(value) {
            if (!installed) field = value
        }

    private var okHttp3Interceptor: Any? = null

    internal var app: Application? = null
        private set(value) {
            if (!installed) field = value
        }

    internal var injectorClassName: String? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (DTSettings.monkeyBlacklist) {
            try {
                MonkeyExcludeActivities.activityStarted(activity)
            } finally {}
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (DTSettings.monkeyBlacklist) {
            try {
                MonkeyExcludeActivities.activityStarted(activity)
            } finally {}
        }
    }

    override fun onActivityResumed(activity: Activity) {
        DTActivityManager.topActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        if (DTActivityManager.topActivity == activity) {
            DTActivityManager.topActivity = null
        }
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    @JvmStatic fun install(app: Application): DTInstaller {
        this.app = app
        MonkeyExcludeActivities.init(app)
        return this
    }

    fun setBlockCanary(context: BlockCanaryContext): DTInstaller {
        blockCanary = context
        return this
    }

    fun setInjector(injector: Injector): DTInstaller {
        this.injector = injector
        return this
    }

    /**
     * @param injector: The package name of injector class
     */
    fun setInjector(injector: String): DTInstaller {
        injectorClassName = injector
        try {
            val injectorClass = Class.forName(injector)
            this.injector = injectorClass.newInstance() as Injector
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun setOkHttpClient(client: Any): DTInstaller {
        httpClient = client
        return this
    }

    fun setHttpLogPath(path: String): DTInstaller {
        DTSettings.httpFileStorePath = path
        return this
    }

    fun setCrashLogPath(path: String): DTInstaller {
        DTSettings.crashFileStorePath = path
        return this
    }

    fun setNotificationIcon(@DrawableRes id: Int): DTInstaller {
        notificationIconRes = id
        return this
    }

    fun setNotificationTitle(title: String): DTInstaller {
        notificationTitle = title
        return this
    }

    fun setNotificationMessage(message: String): DTInstaller {
        notificationMessage = message
        return this
    }

    fun enable(): DTInstaller {
        enabled = true
        return this
    }

    fun disable(): DTInstaller {
        enabled = false
        return this
    }

    fun run() {
        RunningFeatureMgr.clear()
        if(!DTSettings.bottleEnable && enabled)
            return
        RunningFeatureMgr.add(RunningFeatureMgr.DEBUG_BOTTLE)
        installed = true
        if (null != blockCanary) {
            val blockCanary = BlockCanary.install(blockCanary!!)
            if (DTSettings.blockCanaryEnable) {
                blockCanary.start()
                RunningFeatureMgr.add(RunningFeatureMgr.BLOCK_CANARY)
            } else {
                blockCanary.stop()
                RunningFeatureMgr.remove(RunningFeatureMgr.BLOCK_CANARY)
            }
        }
        if (null != app) {

            if (DTSettings.strictMode) {
                enableStrictMode()
                RunningFeatureMgr.add(RunningFeatureMgr.STRICT_MODE)
            } else {
                RunningFeatureMgr.remove(RunningFeatureMgr.STRICT_MODE)
            }

            if (DTSettings.leakCanaryEnable) {
                LeakCanary.install(app)
                RunningFeatureMgr.add(RunningFeatureMgr.LEAK_CANARY)
            } else {
                RunningFeatureMgr.remove(RunningFeatureMgr.LEAK_CANARY)
            }

            showNotification(app!!)
            registerActivityLifecycleCallbacks(app!!)

            if (app?.isSystemAlertPermissionGranted()?: false) {
                app?.runBubbleService()
            }
        }
        if (null != httpClient) {
            OkHttpLoader.load(httpClient)
            DTSettings.networkSniff
        }
        DTCrashHandler.install()
    }

    fun getOkHttp3Interceptor(): Any? {
        try {
            Class.forName(OK_HTTP_CLIENT_3_CLASS)
            if (null == okHttp3Interceptor) {
                okHttp3Interceptor = LoggingInterceptor3()
            }
        } catch (e: Exception) {
            return null
        }
        return okHttp3Interceptor
    }

    internal fun startInject(): Boolean {
        if (null != injector) {
            try {
                injector?.inject()
                return true
            } catch(e: Exception) {
                return false
            }
        }
        return true
    }

    internal fun startTestingEnvironment(): Boolean {
        if (null != injector) {
            try {
                injector?.beforeMonkeyTest()
                return true
            } catch (e: Exception) {
                return false
            }
        }
        return true
    }

    fun setNotificationDisplay(display: Boolean) {
        if (display) {
            showNotification(app!!)
        } else {
            val mNotifyMgr = app?.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
            mNotifyMgr.cancel(NOTIFICATION_ID)
        }
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

    @Suppress("DEPRECATION")
    private fun showNotification(app: Application) {
        //val view = RemoteViews(app.packageName, R.layout.__notification_main)
        //view.setTextViewText(R.id.notify_title, "start")


        //val pi = PendingIntent.getActivity(app, 0, Intent(app, __OnNotificationReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val intent = Intent(app, __OnNotificationReceiver::class.java)
        val pi = PendingIntent.getBroadcast(app, 1, intent, 0)
        val notification: Notification
        val notify = Notification.Builder(app)
        if (null == notificationIconRes) {
            notify.setSmallIcon(R.drawable.__dt_notification_bt)
        } else {
            notify.setSmallIcon(notificationIconRes!!)
        }
                .setLargeIcon(getAppIcon()?.toBitmap())
        if (null == notificationTitle) {
            notify.setContentTitle("Debug Bottle")
        } else {
            notify.setContentTitle(notificationTitle!!)
        }
        if (null == notificationMessage) {
            notify.setContentText("Running with ${app.packageName.split(".").last()}.")
        } else {
            notify.setContentText(notificationMessage)
        }
                .setTicker("debug tool")
                .setContentIntent(pi)
                .setOngoing(true)
        if (SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            notification = notify.notification
        } else {
            notification = notify.build()
        }

        val mNotifyMgr = app.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(NOTIFICATION_ID, notification)
        Log.d(javaClass.simpleName, "started")
    }

    private fun registerActivityLifecycleCallbacks(app: Application) {
        app.registerActivityLifecycleCallbacks(this)
    }

    fun kill() {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        try {
            DTActivityManager.topActivity?.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Toast.makeText(app, R.string.__dt_kill_success, Toast.LENGTH_SHORT).show()
        Process.killProcess(Process.myPid())
    }

    internal fun getSP(fileName: String): SharedPreferences? {
        return app?.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    internal fun getApplication() = app

    internal fun getString(@IdRes id: Int) = app?.getString(id)

    internal fun getAppIcon() = app?.applicationInfo?.loadIcon(app?.packageManager)

    internal fun Drawable.toBitmap(): Bitmap {
        val bitmap: Bitmap?

        if (this is BitmapDrawable) {
            val bitmapDrawable = this
            bitmapDrawable.bitmap?.let { return it }
        }

        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        return bitmap
    }
}