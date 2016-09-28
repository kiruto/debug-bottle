package com.exyui.android.debugbottle.components.fragments

import android.Manifest
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import android.view.*
import android.widget.TextView
import com.exyui.android.debugbottle.components.*
import com.exyui.android.debugbottle.components.bubbles.services.__3DViewBubble
import com.exyui.android.debugbottle.components.bubbles.services.__DTBubble

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
        result.isChecked = __3DViewBubble.isRunning()
        result.setOnCheckedChangeListener { view, isChecked ->
            if (!(context?.isSystemAlertPermissionGranted()?: false)) {
                context?.requestingPermissionDrawOverOtherApps(null)
                result.isChecked = false
            } else {
                if (isChecked) {
                    __3DViewBubble.create(activity)
                } else {
                    __3DViewBubble.destroy(activity)
                }
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

//    private val finishBtn by lazy {
//        val result = findViewById(R.id.__dt_finish_btn)
//        result?.setOnClickListener { context?.finish() }
//        result
//    }

    private val sourceBtn by lazy {
        val result = findViewById(R.id.__dt_source_site)
        result?.setOnClickListener {
//            val url = DTSettings.GITHUB_URL
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse(url)
//            startActivity(intent)
            selectItemAtDrawer(R.string.__dt_project)
        }
        result!!
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
        versionText; procText; procBtn; sourceBtn; refreshView
        setHasOptionsMenu(true)

        // bubble change listener
        registerBubbleStatusChangeReceiver()
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterBubbleStatusChangeReceiver()
    }

    override fun onReceiveBubbleIntent(context: Context, intent: Intent?) {
        when(intent?.extras?.getString(__DTBubble.KEY_TAG)) {
            __3DViewBubble.TAG -> {
                val bubble3DStatus = intent?.extras?.getBoolean(__DTBubble.KEY_IS_RUNNING)?: false
                view3DSwitcher.isChecked = bubble3DStatus
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        menu.add(R.string.__dt_close)
                .setIcon(R.drawable.__ic_close_black_24dp)
                .setOnMenuItemClickListener {
                    activity.finish()
                    true
                }
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
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
                    DTSettings.bottleEnable = true
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

//        if (context?.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            rwPermissionText.granted()
//        } else {
//            rwPermissionText.denied()
//        }
//
//        if (context?.hasPermission(Manifest.permission.READ_PHONE_STATE)) {
            phonePermissionText.granted()
//        } else {
//            phonePermissionText.denied()
//        }
//
//        if (context?.isSystemAlertPermissionGranted(context!!)) {
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