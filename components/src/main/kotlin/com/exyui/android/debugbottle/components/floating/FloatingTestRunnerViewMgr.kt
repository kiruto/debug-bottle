package com.exyui.android.debugbottle.components.floating

import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.exyui.android.debugbottle.components.DTActivityManager
import com.exyui.android.debugbottle.components.DTInstaller
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.testing.MonkeyTestRunner

/**
 * Created by yuriel on 9/19/16.
 */
internal object FloatingTestRunnerViewMgr: DTDragFloatingViewMgr() {
    override val title: String = "Run Test"
    override val bindingService = FloatingTestRunnerService::class

    override fun onClick(v: View) {
        if (!ensureActivity()) return
        showStartDialog()
    }

    private fun ensureActivity(): Boolean {
        val activity = DTActivityManager.topActivity
        if (null == activity){
            val msg = DTInstaller.getString(R.string.__dt_warning_open_activity_first)
            Toast.makeText(DTInstaller.getApplication(), msg, Toast.LENGTH_LONG).show()
            return false
        }
        if (activity.packageName.startsWith("com.exyui")) {
            val msg = DTInstaller.getString(R.string.__dt_warning_joker)
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun showStartDialog() {
        DTBaseFloatingService.stopAllFloatingService()
        AlertDialog.Builder(DTActivityManager.topActivity)
                .setTitle(R.string.__dt_testing_ready)
                .setMessage(R.string.__dt_testing_ready_message)
                .setPositiveButton(R.string.__start) { dialog, witch ->
                    MonkeyTestRunner.start()
                }
                .show()
    }

}