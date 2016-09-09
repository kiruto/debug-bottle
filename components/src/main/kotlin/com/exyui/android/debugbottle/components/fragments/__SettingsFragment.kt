package com.exyui.android.debugbottle.components.fragments

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
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
import com.exyui.android.debugbottle.components.__DTSettings
import com.exyui.android.debugbottle.components.floating.FloatingViewMgr
import com.exyui.android.debugbottle.components.floating.__FloatingService
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
        result.progress = __DTSettings.getBlockThreshold().toInt() - MIN
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
        val result = findViewById(R.id.__dt_network_switcher) as SwitchCompat
        result.isChecked = __DTSettings.getNetworkSniff()
        result.setOnCheckedChangeListener { view, isChecked ->
            __DTSettings.setNetworkSniff(isChecked)
        }
        result
    }

    private val strictSwitcher by lazy {
        val result = findViewById(R.id.__dt_strict_switcher) as SwitchCompat
        result.isChecked = __DTSettings.getStrictMode()
        result.setOnCheckedChangeListener { view, isChecked ->
            __DTSettings.setStrictMode(isChecked)
            if (isChecked) restartHint()
        }
        result
    }

    private val view3DSwitcher by lazy {
        val result = findViewById(R.id.__dt_3d_switcher) as SwitchCompat
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

    private val leakCanarySwitcher by lazy {
        val result = findViewById(R.id.__dt_leak_canary_switcher) as SwitchCompat
        result.isChecked = __DTSettings.getLeakCanaryEnable()
        result.setOnCheckedChangeListener { view, isChecked ->
            __DTSettings.setLeakCanaryEnable(isChecked)
            if (isChecked) restartHint()
        }
        result
    }

    private val blockCanarySwitcher by lazy {
        val result = findViewById(R.id.__dt_block_canary_switcher) as SwitchCompat
        result.isChecked = __DTSettings.getBlockCanaryEnable()
        result.setOnCheckedChangeListener { view, isChecked ->
            __DTSettings.setBlockCanaryEnable(isChecked)
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
        val result = findViewById(R.id.__dt_enable_switcher) as SwitchCompat
        result.isChecked = __DTSettings.getBottleEnable()
        result.setOnCheckedChangeListener { view, isChecked ->
            __DTSettings.setBottleEnable(isChecked)
            if (isChecked) restartHint()
        }
        result
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.__activity_settings, container, false)
        this.rootView = rootView
        seekBar; valueText; networkSwitcher; strictSwitcher; view3DSwitcher
        leakCanarySwitcher; blockCanarySwitcher; bottleSwitch
        return rootView
    }

    override fun onPause() {
        super.onPause()
        save()
    }

    private fun restartHint() {
        Toast.makeText(context, R.string.__dt_need_restart_after_apply, Toast.LENGTH_LONG).show()
    }

    private fun findViewById(@IdRes id: Int) = rootView?.findViewById(id)

    private fun save() {
        __DTSettings.setBlockThreshold(seekBar.progress.toLong())
    }
}