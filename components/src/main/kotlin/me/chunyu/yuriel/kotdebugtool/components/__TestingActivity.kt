package me.chunyu.yuriel.kotdebugtool.components

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import me.chunyu.yuriel.kotdebugtool.components.injector.__InjectorActivity

/**
 * Created by yuriel on 8/11/16.
 */
class __TestingActivity : Activity(), View.OnClickListener {
    val button1 by lazy {
        val result = findViewById(R.id.button1) as Button
        result.setOnClickListener(this)
        result
    }

    val button2 by lazy {
        val result = findViewById(R.id.button2) as Button
        result.setOnClickListener(this)
        result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.__activity_testing)
        button1
        button2
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.button1 -> {
                startActivity(Intent(this, __SettingsActivity::class.java))
            }
            R.id.button2 -> {
                val intent = Intent(this, __InjectorActivity::class.java)
                intent.putExtra(__InjectorActivity.TYPE, __InjectorActivity.TYPE_ALL_ACTIVITIES)
                startActivity(intent)
            }
        }
    }
}