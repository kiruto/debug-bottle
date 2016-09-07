package com.exyui.android.debugbottle.views

import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode

/**
 * Created by yuriel on 8/25/16.
 */
internal object LeakCanaryUi {
    val LIGHT_GREY = 0xFFbababa.toInt()
    val ROOT_COLOR = 0xFF84a6c5.toInt()
    val LEAK_COLOR = 0xFFb1554e.toInt()

    val CLEAR_XFER_MODE = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    /**
     * Converts from device independent pixels (dp or dip) to
     * device dependent pixels. This method returns the input
     * multiplied by the display's density. The result is not
     * rounded nor clamped.

     * The value returned by this method is well suited for
     * drawing with the Canvas API but should not be used to
     * set layout dimensions.

     * @param dp The value in dp to convert to pixels
     * *
     * @param resources An instances of Resources
     */
    fun dpToPixel(dp: Float, resources: Resources): Float {
        val metrics = resources.displayMetrics
        return metrics.density * dp
    }
}
