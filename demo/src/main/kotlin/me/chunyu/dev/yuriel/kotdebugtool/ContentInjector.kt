package me.chunyu.dev.yuriel.kotdebugtool

import android.content.Intent
import android.widget.Toast
import me.chunyu.yuriel.kotdebugtool.components.injector.Injector

/**
 * Created by yuriel on 9/4/16.
 */
class ContentInjector: Injector() {
    override fun inject() {
        put (Intent(activity, DemoActivity::class.java), "activity entry")

        put (Runnable {
            Toast.makeText(activity, "Run!", Toast.LENGTH_SHORT).show()
        }, "runnable entry")
    }
}