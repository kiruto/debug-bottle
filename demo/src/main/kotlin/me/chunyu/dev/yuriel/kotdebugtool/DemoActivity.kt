package me.chunyu.dev.yuriel.kotdebugtool

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import com.exyui.android.debugbottle.components.injector.IntentInjector

/**
 * Created by yuriel on 8/9/16.
 */
class DemoActivity : AppCompatActivity() {

    private val TAG = "DemoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.requestFeature(Window.FEATURE_ACTION_BAR)
        supportActionBar?.hide()
        setContentView(R.layout.activity_demo)
        if (null == supportFragmentManager.findFragmentByTag(TAG)) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, DemoFragment.newInstance(), TAG)
                    .commit()
        }
    }

    fun quickEntryTestFunction() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.shake_anim)
        fun View.shake() {
            startAnimation(animation)
        }

        fun ViewGroup.shakeChild() {
            for (i in 0..childCount) {
                val v = getChildAt(i)
                if (v is ViewGroup) {
                    v.shakeChild()
                } else {
                    v?.shake()
                }
            }
        }

        (window.decorView.rootView as ViewGroup).shakeChild()
    }
}
