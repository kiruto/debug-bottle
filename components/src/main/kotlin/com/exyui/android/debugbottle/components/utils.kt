@file:Suppress("unused")

package com.exyui.android.debugbottle.components

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import android.widget.Toast
import com.exyui.android.debugbottle.components.bubbles.services.__BubblesManagerService

/**
 * Created by yuriel on 9/28/16.
 */
internal fun Int.dp() = this * Resources.getSystem().displayMetrics.density
internal fun Int.px() = this / Resources.getSystem().displayMetrics.density
internal fun Int.sp() = this / Resources.getSystem().displayMetrics.scaledDensity
internal fun Float.dp() = this * Resources.getSystem().displayMetrics.density
internal fun Float.px() = this / Resources.getSystem().displayMetrics.density

/**
 * If bubble not attached to window, should request this permission first.
 */
internal fun Context.requestingPermissionDrawOverOtherApps(msg: String?) {
    val str = msg?: getString(R.string.__dt_draw_over_permission_denied)
    Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

internal fun Context.hasPermission(permission: String) = true/*PackageManager.PERMISSION_DENIED != ContextCompat.checkSelfPermission(this, permission)*/

/**
 * Ensure if application has permission to draw over other app
 */
@TargetApi(Build.VERSION_CODES.M)
internal fun Context.isSystemAlertPermissionGranted(): Boolean {
    val result = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)
    return result
}

/**
 * Initialize bubble manager
 */
internal fun Context.runBubbleService() {
//    try {
        val intent = Intent(this, __BubblesManagerService::class.java)
        this.startService(intent)
//        return "ok"
//    } catch (e: WindowManager.BadTokenException) {
//        app?.requestingPermissionDrawOverOtherApps(null)
//        return ""
//    } catch (e: SecurityException) {
//        app?.requestingPermissionDrawOverOtherApps(e.message)
//        return e.message?: ""
//    } catch (e: Exception) {
//        e.printStackTrace()
//        return "failed"
//    }
}

internal fun Activity.openInBrowser(url: String) {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    startActivity(i)
}