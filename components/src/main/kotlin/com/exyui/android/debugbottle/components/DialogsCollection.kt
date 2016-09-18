package com.exyui.android.debugbottle.components

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.exyui.android.debugbottle.components.widgets.DebugToolEditText
import java.util.regex.Pattern

/**
 * Created by yuriel on 8/16/16.
 */
internal object DialogsCollection {
    class RunActivityDialogFragment: DialogFragment() {

        companion object {
            fun newInstance(intent: Intent): RunActivityDialogFragment {
                val result = RunActivityDialogFragment()
                result.intent = intent
                return result
            }
        }

        private var intent: Intent? = null
        private val intentExtras: MutableMap<String, Any> = mutableMapOf()
        private val inflater by lazy {
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {
            if (null == intent) return null
            for (i in intent?.extras?.keySet()?: setOf()) {
                intentExtras.put(i, intent!!.extras.get(i))
            }
            val builder = AlertDialog.Builder(activity)
            val content = inflater.inflate(R.layout.__dialog_run_activity, null)
            builder.setView(content)
            builder.setPositiveButton(R.string.__run) { dialog, view ->
                for ((k, v) in intentExtras) {
                    if (v is Boolean) {
                        intent!!.putExtra(k, v)
                    } else if (v is Int) {
                        intent!!.putExtra(k, v)
                    } else if (v is Float) {
                        intent!!.putExtra(k, v)
                    } else {
                        intent!!.putExtra(k, v.toString())
                    }
                }
                activity.startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.__cancel) { dialog, view ->
                dialog.dismiss()
            }

            content.init()

            return builder.create()
        }

        private fun View.init() {
            val content = findViewById(R.id.__dt_intent_content) as ViewGroup
            val nameView = findViewById(R.id.__dt_activity_name) as TextView
            val key = findViewById(R.id.__dt_intent_key) as TextView
            val et = findViewById(R.id.__dt_edit_text) as DebugToolEditText
            val title = findViewById(R.id.__dt_title) as TextView
            val putBtn = findViewById(R.id.__dt_edit_submit) as Button
            nameView.text = intent!!.component.className
            title.text = getString(R.string.__run_activity_with_intent)

            et.addTextChangedListener(object: TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, after: Int) {
                    if (key.text.isEmpty()) {
                        if (s?.contains(" ") ?: false) {
                            val string = s!!.split(" ")
                            key.text = string[0]
                            et.setText(string[1])
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            et.onBackPressed {
                if (key.text.isEmpty()) return@onBackPressed
                if (et.text.isEmpty()) {
                    et.setText(key.text.toString())
                    key.text = ""
                }
            }

            et.onSpacePressed {
                if (!key.text.isEmpty()) return@onSpacePressed
                key.text = et.text
                et.setText("")
            }

            fun putExtra() {
                if (!key.text.isEmpty() && !et.text.isEmpty()) {
                    val valueResult: String = et.text.toString()
                    if (valueResult.equals("true", true) || valueResult.equals("false", true)) {
                        intentExtras.put(key.text.toString(), valueResult.toBoolean())
                    } else if (valueResult.isInteger()) {
                        intentExtras.put(key.text.toString(), valueResult.toInt())
                    } else if (valueResult.isNumeric()) {
                        intentExtras.put(key.text.toString(), valueResult.toFloat())
                    } else {
                        intentExtras.put(key.text.toString(), valueResult)
                    }
                    key.text = ""
                    et.setText("")
                    updateContentGroup(content)
                }
            }

            et.onEnterPressed {
                putExtra()
            }

            putBtn.setOnClickListener { putExtra() }
        }

        private fun updateContentGroup(parent: ViewGroup) {
            parent.removeAllViews()
            val onDelClickListener = { v: View ->
                @Suppress("UNCHECKED_CAST")
                val p = v.tag as Pair<String, Any>
                intentExtras.remove(p.first)
                updateContentGroup(parent)
            }
            for ((k, v) in intentExtras) {
                val view = inflater.inflate(R.layout.__item_intents_content, parent, false)
                val del = view.findViewById(R.id.__dt_delete)
                val tv = view.findViewById(R.id.__dt_intent_content_kv) as TextView
                val anyBtn = view.findViewById(R.id.__dt_radio_any) as RadioButton
//                val stringBtn = view.findViewById(R.id.__dt_radio_string) as RadioButton
                val radioGroup = view.findViewById(R.id.__dt_radio_group) as RadioGroup

                del.tag = Pair(k, v)
                del.setOnClickListener(onDelClickListener)

                radioGroup.visibility = View.VISIBLE
                if (v !is String) {
                    radioGroup.check(R.id.__dt_radio_any)
                    anyBtn.text = v.javaClass.simpleName
                    radioGroup.setOnCheckedChangeListener { radioGroup, id ->
                        when(id) {
                            R.id.__dt_radio_any -> intentExtras.put(k, v)
                            R.id.__dt_radio_string -> intentExtras.put(k, v.toString())
                        }
                    }
                } else {
                    radioGroup.check(R.id.__dt_radio_string)
                    if (v.toUpperCase() == "TRUE" || v.toUpperCase() == "FALSE") {
                        anyBtn.text = "Boolean"
                        radioGroup.setOnCheckedChangeListener { radioGroup, id ->
                            when(id) {
                                R.id.__dt_radio_any -> intentExtras.put(k, v.toBoolean())
                                R.id.__dt_radio_string -> intentExtras.put(k, v.toString())
                            }
                        }
                    } else if (v.isInteger()) {
                        anyBtn.text = "Int"
                        radioGroup.setOnCheckedChangeListener { radioGroup, id ->
                            when(id) {
                                R.id.__dt_radio_any -> intentExtras.put(k, v.toInt())
                                R.id.__dt_radio_string -> intentExtras.put(k, v.toString())
                            }
                        }
                    } else if (v.isNumeric()) {
                        anyBtn.text = "Float"
                        radioGroup.setOnCheckedChangeListener { radioGroup, id ->
                            when(id) {
                                R.id.__dt_radio_any -> intentExtras.put(k, v.toFloat())
                                R.id.__dt_radio_string -> intentExtras.put(k, v.toString())
                            }
                        }
                    } else {
                        radioGroup.visibility = View.GONE
                    }
                }
                tv.text = "$k: $v"
                parent.addView(view)
            }
        }

        private fun String.isNumeric(): Boolean {
            val pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*")
            return pattern.matcher(this).matches()
        }

        private fun String.isInteger(): Boolean {
            val pattern = Pattern.compile("^[-\\+]?[\\d]*$")
            return pattern.matcher(this).matches()
        }
    }

    class EditSPDialogFragment: DialogFragment() {

        companion object {
            fun newInstance(key: String, sp: SharedPreferences): EditSPDialogFragment {
                val dialog = EditSPDialogFragment()
                dialog.sp = sp
                dialog.key = key
                return dialog
            }
        }

        private var sp: SharedPreferences? = null
        private var key: String? = null
        private var action: SPDialogAction? = null

        private val inflater by lazy {
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {
            if (null == sp || null == key) return null
            val builder = AlertDialog.Builder(activity)
            val content = inflater.inflate(R.layout.__dialog_sp_edit, null)

            val keyView = content.findViewById(R.id.__dt_sp_key) as TextView
            val editView = content.findViewById(R.id.__dt_sp_value) as TextView
            val radio = content.findViewById(R.id.__dt_radio_group) as RadioGroup

            var value: Any? = null
            for ((k, v) in sp?.all?: mapOf<String, Any>()) {
                if (k == key) {
                    value = v
                    break
                }
            }

            if (value is Boolean) {
                editView.visibility = View.GONE
                radio.visibility = View.VISIBLE
                when(value) {
                    true -> radio.check(R.id.__dt_radio_true)
                    false -> radio.check(R.id.__dt_radio_false)
                }
            } else {
                editView.visibility = View.VISIBLE
                radio.visibility = View.GONE
                editView.text = value.toString()
            }

            when(value) {
                is Int,
                is Float,
                is Double -> editView.setRawInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
            }

            keyView.text = key

            builder.setView(content)
            builder.setNeutralButton(R.string.__delete) { dialog, view ->
                sp?.edit()?.remove(key)
                dialog.dismiss()
                action?.updateSPViews()
            }
            .setNegativeButton(R.string.__cancel) { dialog, view ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.__save) { dialog, view ->
                try {
                    val editor = sp?.edit()?: return@setPositiveButton
                    when (value) {
                        is Boolean -> {
                            val result = radio.checkedRadioButtonId == R.id.__dt_radio_true
                            editor.putBoolean(key, result)
                        }
                        is Int -> editor.putInt(key, editView.text.toString().toInt())
                        is Float -> editor.putFloat(key, editView.text.toString().toFloat())
                        is String -> editor.putString(key, editView.text.toString())
                    }
                    editor.apply()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                dialog.dismiss()
                action?.updateSPViews()
            }
            return builder.create()
        }

        @Suppress("DEPRECATION", "OverridingDeprecatedMember")
        override fun onAttach(activity: Activity?) {
            super.onAttach(activity)
            action = activity as SPDialogAction
        }
    }

    interface SPDialogAction {
        fun updateSPViews()
    }
}