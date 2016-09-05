package me.chunyu.yuriel.kotdebugtool.components.floating

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import me.chunyu.yuriel.kotdebugtool.components.RunningFeatureMgr

/**
 * Created by yuriel on 9/1/16.
 */
internal class __FloatingService : Service() {

    private var binder: Binder? = null

    override fun onCreate() {
        super.onCreate()
        createView()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private fun createView() {
        FloatingViewMgr.setupWith(this)

        fun requestingPermission(msg: String?) {
            val str = msg?:  "Permission denied for this action. You need to manually grant the permission in Settings -> Apps -> Draw over other apps."
            Toast.makeText(this, str, Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        try {
            FloatingViewMgr.show3DViewFloating()
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
        RunningFeatureMgr.add(RunningFeatureMgr.VIEW_3D_WINDOW)
    }

    override fun onDestroy() {
        super.onDestroy()
        FloatingViewMgr.release3DView()
        FloatingViewMgr.releaseContext(this)
        RunningFeatureMgr.remove(RunningFeatureMgr.VIEW_3D_WINDOW)
    }

    inner class FloatingBinder: Binder() {
        fun getService(): __FloatingService {
            return this@__FloatingService
        }
    }
}