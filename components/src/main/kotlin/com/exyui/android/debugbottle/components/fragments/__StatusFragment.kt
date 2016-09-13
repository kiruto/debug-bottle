package com.exyui.android.debugbottle.components.fragments

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.exyui.android.debugbottle.components.*
import com.exyui.android.debugbottle.components.floating.FloatingViewMgr
import com.exyui.android.debugbottle.components.floating.FloatingService

/**
 * Created by yuriel on 9/3/16.
 */
class __StatusFragment: __ContentFragment() {
    private var rootView: View? = null

    override val TAG = __StatusFragment.TAG

    override val isHome = true

    companion object {
        val TAG = "__StatusFragment"
        internal val permissions = listOf(
                /*Manifest.permission.SYSTEM_ALERT_WINDOW,*/
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        )
    }

    private val versionText by lazy {
        val result = findViewById(R.id.__dt_version_text) as TextView
        result.text = "version: $__DT_VERSION_NAME"
        result
    }

    private val permissionRequestBtn by lazy {
        val result = findViewById(R.id.__dt_permission_request)
//        result?.setOnClickListener {
//            requestPermission()
//        }
        result
    }

    private val view3DHelperText by lazy {
        val result = findViewById(R.id.__dt_3d_crash_helper) as TextView

        result.setOnClickListener {
            val url = "http://stackoverflow.com/questions/36016369/system-alert-window-how-to-get-this-permission-automatically-on-android-6-0-an"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        result
    }

    private val view3DSwitcher by lazy {
        val result = findViewById(R.id.__dt_3d_switcher) as SwitchCompat
        FloatingViewMgr.setupWith(context!!.applicationContext)
        result.isChecked = FloatingViewMgr.isFloatingWindowRunning()
        result.setOnCheckedChangeListener { view, isChecked ->
            val intent = Intent(context, FloatingService::class.java)
            if (isChecked) {
                context?.startService(intent)
            } else {
                context?.stopService(intent)
            }
        }
        result
    }

    private val procText by lazy {
        val result = findViewById(R.id.__dt_application_process) as TextView
        result.text = "process_id=${Process.myPid()}"
        result
    }

    private val procBtn by lazy {
        val result = findViewById(R.id.__dt_kill_process)
        result?.setOnClickListener { DTInstaller.kill() }
        result!!
    }

    private val finishBtn by lazy {
        val result = findViewById(R.id.__dt_finish_btn)
        result?.setOnClickListener { context?.finish() }
        result
    }

    private val refreshView by lazy {
        val result = findViewById(R.id.__dt_refresh)
        result?.setOnClickListener { checkupStatus() }
        result!!
    }

    private val rwPermissionText by lazy { findViewById(R.id.__dt_write_external_storage) as TextView }
    private val phonePermissionText by lazy { findViewById(R.id.__dt_read_phone_state) as TextView }
    private val windowPermissionText by lazy { findViewById(R.id.__dt_system_alert_window) as TextView }
    private val bottleStatusText by lazy { findViewById(R.id.__dt_bottle_feature) as TextView }
    private val networkStatusText by lazy { findViewById(R.id.__dt_net_work_feature) as TextView }
    private val strictStatusText by lazy { findViewById(R.id.__dt_strict_mode_feature) as TextView }
    private val view3DStatusText by lazy { findViewById(R.id.__dt_3d_feature) as TextView }
    private val leakStatusText by lazy { findViewById(R.id.__dt_leak_canary_feature) as TextView }
    private val blockStatusText by lazy { findViewById(R.id.__dt_block_canary_feature) as TextView }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.__fragment_status, container, false)
        this.rootView = rootView
        updatePermissionStatus()
        checkupStatus()
        permissionRequestBtn; view3DHelperText; view3DSwitcher
        versionText; procText; procBtn; finishBtn; refreshView
        return rootView
    }

    private fun showNeedPermissionsDialog() {
        AlertDialog.Builder(context)
                .setTitle(R.string.__dt_need_permissions)
                .setMessage(R.string.__dt_permission_message)
                .setNegativeButton(R.string.__dt_not_now) { dialog, witch -> }
                .setPositiveButton(R.string.__dt_check) { dialog, witch ->
                    requestPermission()
                }
                .show()
    }

    private fun showNeedEnableDialog() {
        AlertDialog.Builder(context)
                .setIcon(R.drawable.__dt_ic_bottle_24dp)
                .setTitle(R.string.__dt_need_enable_dt)
                .setMessage(R.string.__dt_enable_dt_message)
                .setNegativeButton(R.string.__dt_later) { dialog, witch ->
                    context?.finish()
                }
                .setPositiveButton(R.string.__dt_enable) { dialog, witch ->
                    DTSettings.setBottleEnable(true)
                    showNeedKillProcDialog()
                }
                .show()
    }

    private fun showNeedKillProcDialog() {
        AlertDialog.Builder(context)
                .setMessage(R.string.__dt_need_kill_proc)
                .setNegativeButton(R.string.__dt_later) { dialog, witch -> }
                .setPositiveButton(R.string.__dt_kill_process) { dialog, witch ->
                    DTInstaller.kill()
                }
                .show()
    }

    private fun checkupPermission() {

//        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            rwPermissionText.granted()
//        } else {
//            rwPermissionText.denied()
//        }
//
//        if (hasPermission(Manifest.permission.READ_PHONE_STATE)) {
            phonePermissionText.granted()
//        } else {
//            phonePermissionText.denied()
//        }
//
//        if (isSystemAlertPermissionGranted(context!!)) {
            windowPermissionText.granted()
//        } else {
//            windowPermissionText.denied()
//        }
    }

    private fun checkupStatus() {
        if (RunningFeatureMgr.has(RunningFeatureMgr.DEBUG_BOTTLE)) {
            bottleStatusText.running()
        } else {
            bottleStatusText.stopped()
        }

        if (RunningFeatureMgr.has(RunningFeatureMgr.NETWORK_LISTENER)) {
            networkStatusText.running()
        } else {
            networkStatusText.stopped()
        }

        if (RunningFeatureMgr.has(RunningFeatureMgr.STRICT_MODE)) {
            strictStatusText.running()
        } else {
            strictStatusText.stopped()
        }

        if (RunningFeatureMgr.has(RunningFeatureMgr.VIEW_3D_WINDOW)) {
            view3DStatusText.running()
        } else {
            view3DStatusText.stopped()
        }

        if (RunningFeatureMgr.has(RunningFeatureMgr.LEAK_CANARY)) {
            leakStatusText.running()
        } else {
            leakStatusText.stopped()
        }

        if (RunningFeatureMgr.has(RunningFeatureMgr.BLOCK_CANARY)) {
            blockStatusText.running()
        } else {
            blockStatusText.stopped()
        }
    }

    /**
     * How to request permission SYSTEM_ALERT_WINDOW?
     *
     * See:
     * http://stackoverflow.com/questions/36016369/system-alert-window-how-to-get-this-permission-automatically-on-android-6-0-an
     */
    fun updatePermissionStatus() {
        if (!ensurePermission()) {
            permissionRequestBtn?.visibility = View.VISIBLE
            showNeedPermissionsDialog()
        } else {
            permissionRequestBtn?.visibility = View.INVISIBLE
            if (!RunningFeatureMgr.has(RunningFeatureMgr.DEBUG_BOTTLE)) {
                showNeedEnableDialog()
            }
        }
    }

    private fun TextView.granted() {
        setText(R.string.__dt_granted)
        setTextColor(Color.GREEN)
    }

    private fun TextView.denied() {
        setText(R.string.__dt_denied)
        setTextColor(Color.RED)
    }

    private fun TextView.running() {
        setText(R.string.__dt_running)
        setTextColor(Color.GREEN)
    }

    private fun TextView.stopped() {
        setText(R.string.__dt_stopped)
        setTextColor(Color.RED)
    }

//    @TargetApi(Build.VERSION_CODES.M)
//    private fun isSystemAlertPermissionGranted(context: Context): Boolean {
//        val result = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)
//        return result
//    }

//    private fun hasPermission(permission: String) = PackageManager.PERMISSION_DENIED != ContextCompat.checkSelfPermission(context!!, permission)

    private fun ensurePermission(): Boolean {
//        context?: return false
//        checkupPermission()
//
//        for (p in permissions) {
//            if (!hasPermission(p))
//                return false
//        }
        return true
    }

    private fun requestPermission() {
//        context?: return
//
//        for (p in permissions) {
//            // Here, thisActivity is the current activity
//            if (ContextCompat.checkSelfPermission(context!!, p) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(context!!, arrayOf(p), permissions.indexOf(p))
//            }
//        }
    }

    private fun findViewById(@IdRes id: Int): View? {
        return rootView?.findViewById(id)
    }
}