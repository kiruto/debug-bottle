package com.exyui.android.debugbottle.components.guide

import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.exyui.android.debugbottle.components.Installer
import com.exyui.android.debugbottle.components.R

/**
 * Created by yuriel on 9/10/16.
 */
internal class __IntroductionActivity: AppCompatActivity() {

    private var theme: Int? = null

    private val pagerView by lazy {
        val result = findViewById(R.id.__dt_view_pager) as ViewPager
        result.adapter = __IntroductionAdapter(supportFragmentManager)
        result.setPageTransformer(false, IntroductionTransformer())
        result
    }

    private val contentView by lazy {
        val result = findViewById(R.id.__dt_content_view) as ViewGroup
        result
    }

    private val backgroundView by lazy {
        val result = findViewById(R.id.__dt_background)
        result
    }

    private val iconApp by lazy {
        val result = findViewById(R.id.__dt_icon_app) as ImageView
        result.setImageDrawable(applicationInfo.loadIcon(packageManager))
        result
    }

    private val iconArrow by lazy {
        val result = findViewById(R.id.__dt_icon_arrow) as ImageView
        result
    }

    private val iconBottle by lazy {
        val result = findViewById(R.id.__dt_icon_bottle) as ImageView
        result
    }

    private val textView by lazy {
        val result = findViewById(R.id.__dt_intro_text) as ViewGroup
        result
    }

    private val nextView by lazy {
        val result = findViewById(R.id.__dt_intro_next) as TextView
        result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            theme = intent.extras.getInt("theme")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "You should not access this activity.", Toast.LENGTH_SHORT).show()
            finish()
        }
        setTheme(theme!!)
        setContentView(R.layout.__activity_introductor)
        supportActionBar?.hide()
        pagerView; contentView
        iconApp; iconArrow; iconBottle
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private inner class IntroductionTransformer: ViewPager.PageTransformer {
        private val TAG = "IntroductionTransformer"

        var start: Int? = null
        var end: Int? = null
        var per: Float? = null

        var iconBottleFrom: Float? = null
        var iconBottleTo: Float? = null
        var iconArrowFrom: Float? = null
        var iconArrowTo: Float? = null
        var iconAppFrom: Float? = null
        var iconAppTo: Float? = null

        var textFrom: Float? = null
        var textTo: Float? = null

        var done: Boolean? = null

        override fun transformPage(view: View?, position: Float) {
            if (position <= -1.0f || position >= 1.0f) {

                // The page is not visible. This is a good place to stop
                // any potential work / animations you may have running.

            } else if (position == 0.0f) {

                // The page is selected. This is a good time to reset Views
                // after animations as you can't always count on the PageTransformer
                // callbacks to match up perfectly.
                val tag = view?.tag as __FragmentPageTag?
                tag ?: return
                backgroundView.setBackgroundColor(tag.color)

                iconBottle.imageAlpha = tag.iconBottleAlpha.toAlphaInt()
                iconArrow.imageAlpha = tag.iconArrowAlpha.toAlphaInt()
                iconApp.imageAlpha = tag.iconAppAlpha.toAlphaInt()
                textView.alpha = tag.text
                if (tag.done) {
                    nextView.setText(R.string.__dt_restart)
                    nextView.setOnClickListener { Installer.kill() }
                } else {
                    nextView.setText(R.string.__dt_next)
                    nextView.setOnClickListener { pagerView.setCurrentItem(pagerView.currentItem + 1, true) }
                }

            } else if (position == -1f) {


            } else {

                val tag = view?.tag as __FragmentPageTag?
                tag?: return
                if (position < 0) {
                    start = tag.color
                    iconBottleFrom = tag.iconBottleAlpha
                    iconArrowFrom = tag.iconArrowAlpha
                    iconAppFrom = tag.iconAppAlpha
                    textFrom = tag.text
                } else {
                    end = tag.color
                    iconBottleTo = tag.iconBottleAlpha
                    iconArrowTo = tag.iconArrowAlpha
                    iconAppTo = tag.iconAppAlpha
                    textTo = tag.text
                    per = 1F - position
                }
                if (null != start && null != end) {
                    val color = calColor(start!!, end!!, per!!)
                    backgroundView.setBackgroundColor(color)
                    calAlpha(iconBottleFrom!!, iconBottleTo!!,
                             iconArrowFrom!!, iconArrowTo!!,
                             iconAppFrom!!, iconAppTo!!,
                             textFrom!!, textTo!!,
                             per!!)
                }

                //Log.d(TAG, "$view: $position, $iconBottleFrom")
            }
        }

        private fun calColor(start: Int, end: Int, per: Float): Int {
            val R = Color.red(start) + (Color.red(end) - Color.red(start)) * per
            val G = Color.green(start) + (Color.green(end) - Color.green(start)) * per
            val B = Color.blue(start) + (Color.blue(end) - Color.blue(start)) * per

            return Color.rgb(R.toInt(), G.toInt(), B.toInt())
        }

        private fun calAlpha(bottleFrom: Float, bottleTo: Float,
                             arrowFrom: Float, arrowTo:Float,
                             appFrom: Float, appTo: Float,
                             textFrom: Float, textTo: Float,
                             per: Float) {
            val bottle = bottleFrom + (bottleTo - bottleFrom) * per
            val arrow = arrowFrom + (arrowTo - arrowFrom) * per
            val app = appFrom + (appTo - appFrom) * per
            var text = textFrom + (textTo - textFrom) * per

            if (text < 0) {
                text = 0f
            }

            iconBottle.imageAlpha = bottle.toAlphaInt()
            iconArrow.imageAlpha = arrow.toAlphaInt()
            iconApp.imageAlpha = app.toAlphaInt()
            textView.alpha = text
        }

        private fun Float.toAlphaInt(): Int {
            if (0f > this) return 0
            return (255 * this).toInt()
        }
    }
}