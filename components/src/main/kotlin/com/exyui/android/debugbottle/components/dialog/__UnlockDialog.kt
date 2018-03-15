package com.exyui.android.debugbottle.components.dialog

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.exyui.android.debugbottle.components.DTSettings
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.widgets.__FloatAnimatedDialog
import com.exyui.android.debugbottle.components.widgets.__FloatingDialogHeaderLayout

/**
 * Created by yuriel on 11/9/16.
 */
class __UnlockDialog: __FloatAnimatedDialog() {

    companion object {
        const val TAG = "__UnlockDialog"
    }

    override val TAG = __UnlockDialog.TAG
    override val title = R.string.__dt_unlock

    private val code = generateCode().toString()

    private var rootView: ViewGroup? = null
    private val numberView by lazy { rootView?.findViewById<TextView>(R.id.__dt_number)!! }
    private val inputView by lazy { rootView?.findViewById<EditText>(R.id.__dt_input)!! }

    override fun createView(): View {
        return activity.layoutInflater.inflate(R.layout.__dialog_unlock, null).apply {
            findViewById<__FloatingDialogHeaderLayout>(R.id.__floating_header).let { header ->
                header.setAction { Toast.makeText(activity, R.string.__dt_notification_locked, Toast.LENGTH_SHORT).show() }
                header.setClose { dismiss() }
            }
            this@__UnlockDialog.rootView = this as ViewGroup
            bindViews()
        }
    }

    private fun bindViews() {
        numberView.text = code
        inputView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (code == input) {
                    DTSettings.notificationLock = false
                    DTSettings.monkeyBlacklist = false
                    Toast.makeText(activity, R.string.__dt_unlocked, Toast.LENGTH_SHORT).show()
                    dismiss()
                } else if (!code.startsWith(input)) {
                    dismiss()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun generateCode(): Int = 100000 + (Math.random() * 900000).toInt()
}