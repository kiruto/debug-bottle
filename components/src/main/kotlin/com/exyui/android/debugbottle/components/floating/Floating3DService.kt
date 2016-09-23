package com.exyui.android.debugbottle.components.floating

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.exyui.android.debugbottle.components.RunningFeatureMgr

/**
 * Created by yuriel on 9/1/16.
 *
 * 3D View floating window management service
 */
@Deprecated(
        message = "Use __BubblesManagerService instead",
        replaceWith = ReplaceWith("__BubblesManagerService", "com.exyui.android.debugbottle.components.bubbles.services.__BubblesManagerService"),
        level = DeprecationLevel.WARNING
)
internal class Floating3DService : DTBaseFloatingService() {

    override val floatingViewMgr: DTDragFloatingViewMgr = Floating3DViewMgr
    private var binder: Binder? = null

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun createView() {
        super.createView()
        RunningFeatureMgr.add(RunningFeatureMgr.VIEW_3D_WINDOW)
    }

    override fun onDestroy() {
        super.onDestroy()
        RunningFeatureMgr.remove(RunningFeatureMgr.VIEW_3D_WINDOW)
    }

    inner class FloatingBinder: Binder() {
        fun getService(): Floating3DService {
            return this@Floating3DService
        }
    }
}