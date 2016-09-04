package me.chunyu.yuriel.kotdebugtool.components.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.chunyu.yuriel.kotdebugtool.components.Installer
import me.chunyu.yuriel.kotdebugtool.components.R
import me.chunyu.yuriel.kotdebugtool.components.floating.FloatingViewMgr
import me.chunyu.yuriel.kotdebugtool.components.floating.__FloatingService

/**
 * Created by yuriel on 9/3/16.
 */
class __StatusFragment: __ContentFragment() {
    private var rootView: View? = null

    override val TAG = __StatusFragment.TAG

    companion object {
        val TAG = "__StatusFragment"
        internal val permissions = listOf(
                /*Manifest.permission.SYSTEM_ALERT_WINDOW,*/
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        )
    }

    private val permissionText by lazy {
        val result = findViewById(R.id.__dt_status_permissions) as TextView
        result
    }

    private val permissionRequestBtn by lazy {
        val result = findViewById(R.id.__dt_permission_request)
        result?.setOnClickListener {
            requestPermission()
        }
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
            val intent = Intent(context, __FloatingService::class.java)
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
        result?.setOnClickListener { Installer.kill() }
        result!!
    }

    private val finishBtn by lazy {
        val result = findViewById(R.id.__dt_finish_btn)
        result?.setOnClickListener { context?.finish() }
        result
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.__fragment_status, container, false)
        this.rootView = rootView
        updatePermissionStatus()
        permissionRequestBtn; view3DHelperText; view3DSwitcher; procText; procBtn; finishBtn
        return rootView
    }

    /**
     * How to request permission SYSTEM_ALERT_WINDOW?
     *
     * See:
     * http://stackoverflow.com/questions/36016369/system-alert-window-how-to-get-this-permission-automatically-on-android-6-0-an
     */
    fun updatePermissionStatus() {
        if (!ensurePermission()) {
            permissionText.setText(R.string.__dt_denied)
            permissionText.setTextColor(Color.RED)
            permissionRequestBtn?.visibility = View.VISIBLE
        } else {
            permissionText.setText(R.string.__dt_granted)
            permissionText.setTextColor(Color.GREEN)
            permissionRequestBtn?.visibility = View.INVISIBLE
        }
    }

    private fun ensurePermission(): Boolean {
        context?: return false

        for (p in permissions) {
            if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(context!!, p))
                return false
        }
        return true
    }

    private fun requestPermission() {
        context?: return

        for (p in permissions) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(context!!, p) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context!!, arrayOf(p), permissions.indexOf(p))
            }
        }
    }

    private fun findViewById(@IdRes id: Int): View? {
        return rootView?.findViewById(id)
    }
}