package com.exyui.android.debugbottle.components.fragments

import android.app.Activity
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.widget.SwitchCompat
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
import com.exyui.android.debugbottle.components.widgets.DTListItemSwitch
import com.exyui.android.debugbottle.ui.BlockCanary

/**
 * Created by yuriel on 9/3/16.
 */
class __SettingsFragment: __ContentFragment() {
    private var rootView: View? = null

    private val MIN: Int = 15
    private val MAX: Int = 5000
    private val DEFAULT: Int = 500

    private val seekBar: SeekBar by lazy {
        val result = findViewById(R.id.__dt_seek_bar) as SeekBar
        result.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
        result.max = MAX - MIN
        result.progress = DTSettings.getBlockThreshold().toInt() - MIN
        result
    }

    private val valueText: EditText by lazy {
        val result = findViewById(R.id.__dt_seek_value) as EditText
        result.addTextChangedListener(object : TextWatcher {
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
        result.setText((seekBar.progress + MIN).toString())
        result
    }

    private val networkSwitcher by lazy {
        val result = findViewById(R.id.__dt_network_switcher) as DTListItemSwitch
        result.isChecked = DTSettings.getNetworkSniff()
        result.setOnCheckedChangeListener { view, isChecked ->
            DTSettings.setNetworkSniff(isChecked)
        }
        result
    }

    private val strictSwitcher by lazy {
        val result = findViewById(R.id.__dt_strict_switcher) as DTListItemSwitch
        result.isChecked = DTSettings.getStrictMode()
        result.setOnCheckedChangeListener { view, isChecked ->
            DTSettings.setStrictMode(isChecked)
            restartHint()
        }
        result
    }

    private val view3DSwitcher by lazy {
        val result = findViewById(R.id.__dt_3d_switcher) as DTListItemSwitch
        result.isChecked = __3DViewBubble.isRunning()
        result.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                __3DViewBubble.create(activity.applicationContext)
            } else {
                __3DViewBubble.destroy(activity.applicationContext)
            }
        }
        result
    }

    private val leakCanarySwitcher by lazy {
        val result = findViewById(R.id.__dt_leak_canary_switcher) as DTListItemSwitch
        result.isChecked = DTSettings.getLeakCanaryEnable()
        result.setOnCheckedChangeListener { view, isChecked ->
            DTSettings.setLeakCanaryEnable(isChecked)
            restartHint()
        }
        result
    }

    private val blockCanarySwitcher by lazy {
        val result = findViewById(R.id.__dt_block_canary_switcher) as SwitchCompat
        result.isChecked = DTSettings.getBlockCanaryEnable()
        result.setOnCheckedChangeListener { view, isChecked ->
            DTSettings.setBlockCanaryEnable(isChecked)
            try {
                if (isChecked) {
                    BlockCanary.get().start()
                } else {
                    BlockCanary.get().stop()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        result
    }

    private val bottleSwitch by lazy {
        val result = findViewById(R.id.__dt_enable_switcher) as DTListItemSwitch
        result.isChecked = DTSettings.getBottleEnable()
        result.setOnCheckedChangeListener { view, isChecked ->
            DTSettings.setBottleEnable(isChecked)
            restartHint()
        }
        result
    }

    private val bubbleStatusChangeReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                when(intent?.extras?.getString(__DTBubble.KEY_TAG)) {
                    __3DViewBubble.TAG -> {
                        val bubble3DStatus = intent?.extras?.getBoolean(__DTBubble.KEY_IS_RUNNING)?: false
                        view3DSwitcher.isChecked = bubble3DStatus
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.__activity_settings, container, false)
        this.rootView = rootView
        seekBar; valueText; networkSwitcher; strictSwitcher; view3DSwitcher
        leakCanarySwitcher; blockCanarySwitcher; bottleSwitch

        // bubble change listener
        registerBubbleStatusChangeReceiver()
        return rootView
    }

    private fun registerBubbleStatusChangeReceiver() {
        val filter = IntentFilter()
        filter.addAction(__DTBubble.INTENT_ACTION)
        activity.registerReceiver(bubbleStatusChangeReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        save()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity.unregisterReceiver(bubbleStatusChangeReceiver)
    }

    private fun restartHint() {
        Toast.makeText(context, R.string.__dt_need_restart_after_apply, Toast.LENGTH_LONG).show()
    }

    private fun findViewById(@IdRes id: Int) = rootView?.findViewById(id)

    private fun save() {
        DTSettings.setBlockThreshold(seekBar.progress.toLong())
    }
}