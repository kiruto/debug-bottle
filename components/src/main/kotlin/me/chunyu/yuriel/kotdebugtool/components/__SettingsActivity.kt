package me.chunyu.yuriel.kotdebugtool.components

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import me.chunyu.yuriel.kotdebugtool.core.DEFAULT_BLOCK_THRESHOLD

internal class __SettingsActivity : __DTBaseActivity() {

    private val MIN: Int = 100
    private val MAX: Int = 5000
    private val DEFAULT: Int = 500

    override val actionbarTitle: String by lazy { getString(R.string.__dt_setting_title) }

    private val seekBar: SeekBar by lazy {
        val result = findViewById(R.id.__dt_seek_bar) as SeekBar
        result.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
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
        result.addTextChangedListener(object: TextWatcher {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.__activity_settings)
        seekBar; valueText
    }

    override fun onStop() {
        super.onStop()
        save()
    }

    private fun save() {
        __DTSettings.setBlockThreshold(seekBar.progress.toLong())
    }
}
