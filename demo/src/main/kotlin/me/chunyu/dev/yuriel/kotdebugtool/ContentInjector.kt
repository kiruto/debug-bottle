package me.chunyu.dev.yuriel.kotdebugtool

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Process
import android.widget.Toast
import com.exyui.android.debugbottle.components.injector.Injector
import com.exyui.android.debugbottle.components.injector.QuickEntry

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

        quickEntry ("Show Activity's package name", object: QuickEntry.OnActivityDisplayedListener {
            override fun shouldShowEntry(activity: Activity?) = true

            override fun run(context: Context?) {
                Toast.makeText(context, context?.javaClass?.name, Toast.LENGTH_LONG).show()
            }

            override fun description() = "This case shows how to run a function with context."
        })

        quickEntry("Show address of title TextView 'Showcases'", object: QuickEntry.OnActivityDisplayedListener {
            override fun shouldShowEntry(activity: Activity?) = activity is DemoActivity

            override fun run(context: Context?) {
                if (context is DemoActivity) {
                    val view = context.findViewById(R.id.tv_title)
                    Toast.makeText(context, view.toString(), Toast.LENGTH_LONG).show()
                }
            }

            override fun description() = "This case shows how to run a function with views"
        })

        quickEntry("Shake views in Activity without border", object: QuickEntry.OnActivityDisplayedListener {
            override fun shouldShowEntry(activity: Activity?) = activity is DemoActivity

            override fun run(context: Context?) {
                if (context is DemoActivity) {
                    context.quickEntryTestFunction()
                }
            }

            override fun description() = "This case shows how to call a function in activity"
        })
    }
}