package me.chunyu.dev.yuriel.kotdebugtool

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Process
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.exyui.android.debugbottle.components.injector.Injector
import com.exyui.android.debugbottle.components.injector.QuickEntry

/**
 * Created by yuriel on 9/4/16.
 *
 * This example file shows how to inject an Intent a Runnable, or a QuickEntry
 */
class ContentInjector: Injector() {
    override fun inject() {

        /**
         * To inject a simple activity entry, just need call "put" function.
         * The "activity" object is current top Activity.
         * If current activity is not shown at foreground, may return null.
         *
         * About "put" function:
         *  - first parameter for the Intent.
         *  - second parameter for the name of entry.
         *  All arguments to start activity, must put into Intent.
         */
        put (Intent(activity, DemoActivity::class.java), "Demo Activity entry example")

        put (Intent(activity, ExampleActivity::class.java), "Setting Activity example")

        /**
         * To inject a Runnable, also need call "put" function.
         * Also can use "activity" as a context, and activity also return null if it'is not shown at foreground
         * This example just show a toast, but you may do many other amazing things.
         *
         * About "put" function:
         *  - first parameter is the target Runnable object.
         *  - second parameter is the name of Runnable entry.
         */
        put (Runnable {
            Toast.makeText(activity, "Run!", Toast.LENGTH_SHORT).show()
        }, "Toast a \"Run!\"")

        /**
         * Like this, you can clean caches in runnable.
         */
        put (Runnable {
            // Do something to clean the caches.
            // if done,
            Toast.makeText(activity, "Cache files delete!", Toast.LENGTH_SHORT).show()
        }, "Delete cache files example")

        /**
         * Like this, you can kill a task.
         */
        put (Runnable {
            Process.killProcess(Process.myPid())
        }, "Kill process example")

        /**
         * Here is the example of how to use QuickEntry.
         * To implement QuickEntry, you may extend class "QuickEntry.OnActivityDisplayedListener".
         * To add entry, just call "quickEntry" function.
         *
         * About "quickEntry" function:
         *  - first parameter is the name of entry.
         *  - second parameter is the object of QuickEntry's implementation.
         *
         * About "OnActivityDisplayedListener" interface:
         *  - "shouldShowEntry" function returns result that if this entry shows at panel now.
         *  - "run" function is the entry.
         *  - "description" function returns the subtitle of this entry, may return null if it's not needed.
         *
         * This case shows how to work with current context (as an Activity).
         */
        quickEntry ("Show Activity's package name", object: QuickEntry.OnActivityDisplayedListener {
            override fun shouldShowEntry(activity: Activity?) = true

            override fun run(context: Context?) {
                Toast.makeText(context, context?.javaClass?.name, Toast.LENGTH_LONG).show()
            }

            override fun description() = "This case shows how to run a function with context."
        })

        /**
         * This case shows how to hack or test current Activity.
         * If the DemoActivity is not visible at foreground, this entry would not shown.
         * If current Activity is DemoActivity, entry may displayed and run correctly.
         */
        quickEntry("Show address of title TextView 'Showcases'", object: QuickEntry.OnActivityDisplayedListener {
            override fun shouldShowEntry(activity: Activity?) = activity is DemoActivity

            override fun run(context: Context?) {
                if (context is DemoActivity) {
                    val animation = AnimationUtils.loadAnimation(context, R.anim.shake_anim)
                    fun View.shake() {
                        startAnimation(animation)
                    }

                    val view = context.findViewById(R.id.tv_title)
                    view.shake()
                    Toast.makeText(context, view.toString(), Toast.LENGTH_LONG).show()
                }
            }

            override fun description() = "This case shows how to run a function with views"
        })

        /**
         * This case shows how to call a function in DemoActivity.
         */
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