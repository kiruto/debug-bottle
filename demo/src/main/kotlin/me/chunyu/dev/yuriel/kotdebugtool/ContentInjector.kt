package me.chunyu.dev.yuriel.kotdebugtool

import android.content.Intent
import android.os.Process
import android.widget.Toast
import com.exyui.android.debugbottle.components.injector.Injector

/**
 * Created by yuriel on 9/4/16.
 */
class ContentInjector: Injector() {
    override fun inject() {
        put (Intent(activity, DemoActivity::class.java), "Demo Activity entry example")

        put (Intent(activity, ExampleActivity::class.java), "Setting Activity example")

        put (Runnable {
            Toast.makeText(activity, "Run!", Toast.LENGTH_SHORT).show()
        }, "Toast a \"Run!\"")

        put (Runnable {
            Toast.makeText(activity, "Cache files delete!", Toast.LENGTH_SHORT).show()
        }, "Delete cache files example")

        put (Runnable {
            Process.killProcess(Process.myPid())
        }, "Kill process example")
    }
}