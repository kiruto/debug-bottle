package com.exyui.android.debugbottle.components.dialog

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import com.exyui.android.debugbottle.components.*
import com.exyui.android.debugbottle.components.dialog.quickviews.__QuickDialogMainView
import com.exyui.android.debugbottle.components.widgets.__FloatAnimatedDialog
import com.exyui.android.debugbottle.components.widgets.__FloatingDialogHeaderLayout

/**
 * Created by yuriel on 10/12/16.
 */
class __QuickDialog : __FloatAnimatedDialog() {

    companion object {
        val TAG = "__QuickDialog"
    }
    override val TAG = __QuickDialog.TAG
    override val title = R.string.__dt_bottle

    private var rootView: ViewGroup? = null
    private var currentPanel: View? = null
    private val container by lazy {
        rootView?.findViewById(R.id.__dt_container) as ViewGroup
    }

    private val mainView by lazy {
        container.findViewById(R.id.__dt_quick_main) as __QuickDialogMainView
    }

    private val togglesView by lazy {
        container.findViewById(R.id.__dt_quick_toggle) as ViewGroup
    }

    override fun createView(): View {
        val result = activity.layoutInflater.inflate(R.layout.__dialog_toggles, null)

        val header = result.findViewById(R.id.__floating_header) as __FloatingDialogHeaderLayout
        header.setAction {
            if (togglesView sameAs currentPanel) {
                container.show(mainView)
                header.setActionIcon(R.drawable.__ic_settings_black_24dp)
            } else {
                container.show(togglesView)
                header.setActionIcon(R.drawable.__ic_arrow_back_black_24dp)
            }
        }
        header.setClose { dismiss() }
        result.findViewById(R.id.__dt_open).setOnClickListener { startDTDrawerActivity() }
        result.findViewById(R.id.__dt_help).setOnClickListener { help() }
        rootView = result as ViewGroup
        bindViews()
        isCancelable = true

        // Dismiss dialog after click quick item
        mainView.addOnItemClickListener { dismiss() }
        container.show(mainView)
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun bindViews() {

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

    private fun help() {
        activity.openInBrowser("https://github.com/kiruto/debug-bottle/blob/1.0.1/demo/src/main/kotlin/me/chunyu/dev/yuriel/kotdebugtool/ContentInjector.kt")
    }

    private fun ViewGroup.show(view: View) {
        for (i in 0..childCount - 1) {
            val child = getChildAt(i)
            if (view sameAs child) {
                child.visibility = View.VISIBLE
                currentPanel = view
            } else {
                child.visibility = View.GONE
            }
        }
    }

    private infix fun View.sameAs(v: View?) = id == v?.id

}