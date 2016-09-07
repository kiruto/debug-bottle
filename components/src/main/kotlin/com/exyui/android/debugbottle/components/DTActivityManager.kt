package com.exyui.android.debugbottle.components

import android.app.Activity
import kotlin.properties.Delegates

/**
 * Created by yuriel on 9/2/16.
 */
internal object DTActivityManager {
    private var injected = false

    var topActivity by Delegates.observable<Activity?>(null) { prop, old, new ->
        if (!injected && new != null) {
            injected = Installer.startInject()
        }
    }
}