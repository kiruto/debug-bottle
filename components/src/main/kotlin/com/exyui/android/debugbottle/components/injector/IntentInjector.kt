package com.exyui.android.debugbottle.components.injector

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by yuriel on 8/10/16.
 */
object IntentInjector : __IntentInjectorImpl() {
    override val model: LinkedHashMap<String, Intent> = LinkedHashMap()
}