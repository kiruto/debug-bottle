package com.exyui.android.debugbottle.components.dialog

import android.app.FragmentManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.exyui.android.debugbottle.components.DTDrawerActivity
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.bubbles.services.__TestingRunnerBubble
import com.exyui.android.debugbottle.components.testing.StressTestRunner
import com.exyui.android.debugbottle.components.widgets.__FloatAnimatedDialog
import com.exyui.android.debugbottle.components.widgets.__FloatingDialogHeaderLayout

/**
 * Created by yuriel on 9/26/16.
 */
class __TestingPreStartDialog : __FloatAnimatedDialog() {

    companion object {
        val TAG = "__TestingPreStartDialog"
    }

    override val TAG = __TestingPreStartDialog.TAG
    override val title = R.string.__dt_black_box_testing

    override fun createView(): View {
        __TestingRunnerBubble.destroy(activity)
        val result = activity.layoutInflater.inflate(R.layout.__dialog_prestart_testing, null)

        val header = result.findViewById(R.id.__floating_header) as __FloatingDialogHeaderLayout
        header.setAction { openSettings() }
        header.setClose { close() }
        result.findViewById(R.id.__dt_run_test).setOnClickListener { startTesting() }
        isCancelable = false
        return result
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
    }

    /*
    override fun onClick(dialog: DialogInterface?, which: Int) {
        when(which) {
            DialogInterface.BUTTON_POSITIVE -> startTesting()
            DialogInterface.BUTTON_NEGATIVE -> recreateBubble()
        }
    }
    */

    private fun close() {
        recreateBubble()
        dismiss()
    }

    private fun recreateBubble() {
        if (null != activity) {
            __TestingRunnerBubble.create(activity)
        }
    }

    private fun startTesting() {
        dismiss()
        StressTestRunner.start()
    }

    private fun openSettings() {
        val intent = Intent(activity, DTDrawerActivity::class.java)
        intent.putExtra(DTDrawerActivity.KEY_SELECTED, R.string.__dt_black_box_testing)
        activity.startActivity(intent)
    }
}