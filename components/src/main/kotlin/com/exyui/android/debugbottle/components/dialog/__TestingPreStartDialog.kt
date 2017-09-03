package com.exyui.android.debugbottle.components.dialog

import android.content.DialogInterface
import android.content.Intent
import android.view.View
import com.exyui.android.debugbottle.components.DTDrawerActivity
import com.exyui.android.debugbottle.components.DTInstaller
import com.exyui.android.debugbottle.components.DTSettings
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.bubbles.services.__TestingRunnerBubble
import com.exyui.android.debugbottle.components.fragments.components.blockCanarySwitcher
import com.exyui.android.debugbottle.components.fragments.components.leakCanarySwitcher
import com.exyui.android.debugbottle.components.fragments.components.networkSwitcher
import com.exyui.android.debugbottle.components.fragments.components.strictSwitcher
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

    private var rootView: View? = null

    private val networkSwitcher by lazy { rootView?.networkSwitcher(R.id.__dt_network_switcher) }
    private val strictSwitcher by lazy { rootView?.strictSwitcher(R.id.__dt_strict_switcher) }
    private val leakCanarySwitcher by lazy { rootView?.leakCanarySwitcher(R.id.__dt_leak_canary_switcher) }
    private val blockCanarySwitcher by lazy { rootView?.blockCanarySwitcher(R.id.__dt_block_canary_switcher) }

    override fun createView(): View {
        __TestingRunnerBubble.destroy(activity)
        return activity.layoutInflater.inflate(R.layout.__dialog_prestart_testing, null).apply {
            (findViewById(R.id.__floating_header) as __FloatingDialogHeaderLayout).let { header ->
                header.setAction { openSettings() }
                header.setClose { close() }
            }
            findViewById(R.id.__dt_apply_and_restart).setOnClickListener {
                startTestingEnvironment()
                DTInstaller.kill()
            }
            findViewById(R.id.__dt_run_test).setOnClickListener { startTestingEnvironment() }
            isCancelable = false
            this@__TestingPreStartDialog.rootView = this
            bindViews()
        }
    }

    private fun bindViews() {
        networkSwitcher; strictSwitcher; leakCanarySwitcher; blockCanarySwitcher
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
        activity?.let { __TestingRunnerBubble.create(it) }
    }

    private fun startTesting() {
        dismiss()
        StressTestRunner.start()
    }

    private fun startTestingEnvironment() {
        DTSettings.monkeyBlacklist = true
        DTSettings.notificationLock = true
        DTInstaller.startTestingEnvironment()
        dismiss()
    }

    private fun openSettings() {
        Intent(activity, DTDrawerActivity::class.java).apply {
            putExtra(DTDrawerActivity.KEY_SELECTED, R.string.__dt_black_box_testing)
            activity.startActivity(this)
        }
    }
}