package com.exyui.android.debugbottle.components.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.DTSettings
import com.exyui.android.debugbottle.components.bubbles.services.__3DViewBubble
import com.exyui.android.debugbottle.components.bubbles.services.__DTBubble
import com.exyui.android.debugbottle.components.fragments.components.*

/**
 * Created by yuriel on 9/3/16.
 */
class __SettingsFragment: __ContentFragment() {
    private var rootView: View? = null

    private val MIN: Int = 15
    private val MAX: Int = 5000
    private val DEFAULT: Int = 500

    private val seekBar: SeekBar by lazy {
        (findViewById(R.id.__dt_seek_bar) as SeekBar).apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStopTrackingTouch(v: SeekBar?) {

                }

                override fun onStartTrackingTouch(v: SeekBar?) {

                }

                override fun onProgressChanged(v: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        valueText.setText((progress + MIN).toString())
                    }
                }
            })
            max = MAX - MIN
            progress = DTSettings.blockThreshold.toInt() - MIN
        }
    }

    private val valueText: EditText by lazy {
        (findViewById(R.id.__dt_seek_value) as EditText).apply {
            addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    try {
                        var num: Int = s.toString().toInt()
                        if (num < MIN || num > MAX) {
                            num = DEFAULT
                        }
                        seekBar.progress = num
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
            setText((seekBar.progress + MIN).toString())
        }
    }

    private val networkSwitcher by lazy { rootView?.networkSwitcher(R.id.__dt_network_switcher) }
    private val strictSwitcher by lazy { rootView?.strictSwitcher(R.id.__dt_strict_switcher) }
    private val view3DSwitcher by lazy { rootView?.view3DSwitcher(R.id.__dt_3d_switcher) }
    private val leakCanarySwitcher by lazy { rootView?.leakCanarySwitcher(R.id.__dt_leak_canary_switcher) }
    private val blockCanarySwitcher by lazy { rootView?.blockCanarySwitcherCompat(R.id.__dt_block_canary_switcher) }
    private val bottleSwitch by lazy { rootView?.bottleSwitch(R.id.__dt_enable_switcher) }
    private val frameSwitcher by lazy { rootView?.frameSwitcher(R.id.__dt_frame_switcher) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.__activity_settings, container, false)
        this.rootView = rootView
        seekBar; valueText; networkSwitcher; strictSwitcher; view3DSwitcher
        leakCanarySwitcher; blockCanarySwitcher; bottleSwitch; frameSwitcher

        // bubble change listener
        registerBubbleStatusChangeReceiver()
        return rootView
    }

    override fun onReceiveBubbleIntent(context: Context, intent: Intent?) {
        when(intent?.extras?.getString(__DTBubble.KEY_TAG)) {
            __3DViewBubble.TAG -> {
                val bubble3DStatus = intent.extras?.getBoolean(__DTBubble.KEY_IS_RUNNING)?: false
                view3DSwitcher?.isChecked = bubble3DStatus
            }
        }
    }

    override fun onPause() {
        super.onPause()
        save()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterBubbleStatusChangeReceiver()
    }

    private fun hintRestart() {
        Toast.makeText(context, R.string.__dt_need_restart_after_apply, Toast.LENGTH_LONG).show()
    }

    private fun findViewById(@IdRes id: Int) = rootView?.findViewById(id)

    private fun save() {
        DTSettings.blockThreshold = seekBar.progress.toLong()
    }
}