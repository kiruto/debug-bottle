package me.chunyu.yuriel.kotdebugtool.components

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.squareup.leakcanary.internal.DisplayLeakActivity
import me.chunyu.yuriel.kotdebugtool.components.floating.__FloatingService
import me.chunyu.yuriel.kotdebugtool.components.injector.__InjectorActivity
import me.chunyu.yuriel.kotdebugtool.ui.layout.__DisplayBlockActivity

/**
 * Created by yuriel on 8/11/16.
 */
internal class __TestingActivity : Activity() {
    val button1 by lazy {
        val result = findViewById(R.id.__dt_button1) as Button
        result.setOnClickListener { startActivity(Intent(this, __ExampleActivity::class.java)) }
        result
    }

    val button2 by lazy {
        val result = findViewById(R.id.__dt_button2) as Button
        result.setOnClickListener {
            val intent = Intent(this, __InjectorActivity::class.java)
            intent.putExtra(__InjectorActivity.TYPE, __InjectorActivity.TYPE_ALL_ACTIVITIES)
            startActivity(intent)
        }
        result
    }

    val button3 by lazy {
        val result = findViewById(R.id.__dt_button3) as Button
        result.setOnClickListener {
            val intent = Intent(this, DisplayLeakActivity::class.java)
            startActivity(intent)
        }
        result
    }

    val button4 by lazy {
        val result = findViewById(R.id.__dt_button4) as Button
        result.setOnClickListener {
            val intent = Intent(this, __DisplayBlockActivity::class.java)
            startActivity(intent)
        }
        result
    }

    val button5 by lazy {
        val result = findViewById(R.id.__dt_button5) as Button
        result.setOnClickListener {
            val intent = Intent(this, __FloatingService::class.java)
            startService(intent)
        }
        result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.__activity_testing)
        button1; button2; button3; button4; button5
    }
}