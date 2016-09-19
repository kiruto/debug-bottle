package com.exyui.android.debugbottle.components.floating

import android.view.View

/**
 * Created by yuriel on 9/19/16.
 */
internal object FloatingTestRunnerViewMgr: DTDragFloatingViewMgr() {
    override val title: String = "Run Test"
    override val bindingService = FloatingTestRunnerService::class

    override fun onClick(v: View) {

    }
}