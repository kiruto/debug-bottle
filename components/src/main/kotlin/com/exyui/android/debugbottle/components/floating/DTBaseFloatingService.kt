package com.exyui.android.debugbottle.components.floating

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast

/**
 * Created by yuriel on 9/19/16.
 */
internal abstract class DTBaseFloatingService: Service() {

    abstract val floatingViewMgr: DTDragFloatingViewMgr

    override fun onCreate() {
        super.onCreate()
        createView()
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingViewMgr.releaseView()
        floatingViewMgr.releaseContext(this)
    }

    open fun createView() {
        floatingViewMgr.setupWith(this)

        try {
            floatingViewMgr.showFloatingView()
        } catch (e: WindowManager.BadTokenException) {
            requestingPermission(null)
            return
        } catch (e: SecurityException) {
            requestingPermission(e.message)
            return
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

    }

    fun requestingPermission(msg: String?) {
        val str = msg?:  "Permission denied for this action. You need to manually grant the permission in Settings -> Apps -> Draw over other apps."
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}