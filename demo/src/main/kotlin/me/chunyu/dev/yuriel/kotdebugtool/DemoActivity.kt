package me.chunyu.dev.yuriel.kotdebugtool

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import me.chunyu.yuriel.kotdebugtool.components.injector.IntentInjector

/**
 * Created by yuriel on 8/9/16.
 */
class DemoActivity : AppCompatActivity() {

    private val TAG = "DemoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        supportActionBar?.hide()
        setContentView(R.layout.__activity_demo)
        if (null == supportFragmentManager.findFragmentByTag(TAG)) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, DemoFragment.newInstance(), TAG)
                    .commit()
        }
    }
}
