package com.exyui.android.debugbottle.components.injector

import android.content.Intent
import java.util.*

/**
 * Created by yuriel on 9/4/16.
 */
interface InjectHelper {
    fun putIntent(intents: ArrayList<Intent>): ArrayList<Intent>
    fun putRunnable(runnable: ArrayList<Runnable>): ArrayList<Runnable>
}