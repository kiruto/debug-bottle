package com.exyui.android.debugbottle.components.dialog

import android.content.Intent
import android.view.View
import com.exyui.android.debugbottle.components.DTActivityManager
import com.exyui.android.debugbottle.components.DTDrawerActivity
import com.exyui.android.debugbottle.components.DTInstaller
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.fragments.components.*
import com.exyui.android.debugbottle.components.widgets.__FloatAnimatedDialog
import com.exyui.android.debugbottle.components.widgets.__FloatingDialogHeaderLayout

/**
 * Created by yuriel on 10/12/16.
 */
class __TogglesDialog: __FloatAnimatedDialog() {

    companion object {
        val TAG = "__TogglesDialog"
    }
    override val TAG = __TogglesDialog.TAG
    override val title = R.string.__dt_toggles

    override fun createView(): View {
        val result = activity.layoutInflater.inflate(R.layout.__dialog_toggles, null)

        val header = result.findViewById(R.id.__floating_header) as __FloatingDialogHeaderLayout
        header.setAction { startDTDrawerActivity(true) }
        header.setClose { dismiss() }
        result.findViewById(R.id.__dt_run_test).setOnClickListener { startDTDrawerActivity() }
        bindViews(result)
        isCancelable = false
        return result
    }

    private fun bindViews(v: View) {
        v.networkSwitcher(R.id.__dt_network_switcher)
        v.strictSwitcher(R.id.__dt_strict_switcher)
        v.view3DSwitcher(R.id.__dt_3d_switcher)
        v.leakCanarySwitcher(R.id.__dt_leak_canary_switcher)
        v.frameSwitcher(R.id.__dt_frame_switcher)
        v.blockCanarySwitcher(R.id.__dt_block_canary_switcher)
    }

    private fun startDTDrawerActivity(openSettings: Boolean = false) {
        if (DTActivityManager.topActivity?.javaClass == DTDrawerActivity::class.java) {
            dismiss()
        }
        val intent = Intent(DTInstaller.app, DTDrawerActivity::class.java)
        if (openSettings) {
            intent.putExtra(DTDrawerActivity.KEY_SELECTED, R.string.__dt_settings)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        DTInstaller.app?.startActivity(intent)
    }
}