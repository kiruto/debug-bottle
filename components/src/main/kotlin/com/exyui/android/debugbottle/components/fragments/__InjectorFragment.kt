package com.exyui.android.debugbottle.components.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.support.annotation.IdRes
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ListView
import com.exyui.android.debugbottle.components.DTInstaller
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.SearchableListViewHelper
import com.exyui.android.debugbottle.components.injector.AllActivities
import com.exyui.android.debugbottle.components.injector.InjectableAdapter
import com.exyui.android.debugbottle.components.injector.IntentInjector
import com.exyui.android.debugbottle.components.injector.RunnableInjector

/**
 * Created by yuriel on 9/3/16.
 */
class __InjectorFragment: __ContentFragment() {
    private var rootView: View? = null

    companion object {
        val TYPE = "type"
        val TYPE_RUNNABLE = 0x11
        val TYPE_INTENT = 0x22
        val TYPE_ALL_ACTIVITIES = 0x33

        fun newInstance(type: Int): __InjectorFragment {
            val f = __InjectorFragment()
            val arg = Bundle()
            arg.putInt(TYPE, type)
            f.arguments = arg
            return f
        }
    }

    private val type: Int by lazy {
        arguments!!.getInt(TYPE, TYPE_ALL_ACTIVITIES)
    }

    private val adapter by lazy {
        val injectable = when (type) {
            TYPE_RUNNABLE -> RunnableInjector
            TYPE_INTENT -> {
                IntentInjector.setActivity(context!!)
                IntentInjector
            }
            TYPE_ALL_ACTIVITIES -> AllActivities(context!!)
            else -> throw NotImplementedError("$type is not implemented")
        }
        InjectableAdapter(injectable)
    }

    private val editText by lazy {
        (findViewById(R.id.edit_text) as EditText).apply {
            addTextChangedListener(object: TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    val pattern = SearchableListViewHelper.getPattern(s.toString())
                    adapter.highlight(pattern, s.toString())
                }
            })
        }
    }

    private val list by lazy {
        (findViewById(R.id.list_view) as ListView).apply {
            this.adapter = this@__InjectorFragment.adapter
            onItemClickListener = this@__InjectorFragment.adapter
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.__activity_injector, container, false).apply {
            this@__InjectorFragment.rootView = rootView
            list; editText
            if (type != TYPE_ALL_ACTIVITIES) {
                setHasOptionsMenu(true)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        menu.add(R.string.__dt_how_to_use)
                .setIcon(R.drawable.__ic_lightbulb_outline_black_24dp)
                .setOnMenuItemClickListener {
                    showHelper()
                    true
                }
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

    private fun showHelper() {
        val builder = AlertDialog.Builder(activity)
                .setIcon(R.drawable.__ic_lightbulb_outline_black_24dp)
                .setTitle(R.string.__dt_injector_help)
        when(type) {
            TYPE_INTENT -> builder.setMessage(R.string.__dt_intent_injector_introduction)
            TYPE_RUNNABLE -> builder.setMessage(R.string.__dt_runnable_injector_introduction)
            else -> builder
        }
                .setPositiveButton(R.string.__dt_next) { _, _ ->
                    showStepOne()
                }
                .setNegativeButton(R.string.__dt_close) { _, _ -> }
                .show()
    }

    private fun showStepOne() {
        val packageName = DTInstaller.injectorClassName
        AlertDialog.Builder(activity)
                .setIcon(R.drawable.__ic_lightbulb_outline_black_24dp)
                .setTitle(R.string.__dt_step_one)
                .setMessage(
                        if (null == packageName) getString(R.string.__dt_injector_step_one_no_package_message)
                        else getString(R.string.__dt_injector_step_one_message, packageName))
                .setNegativeButton(R.string.__dt_close) { _, _ -> }
                .show()
    }

    private fun findViewById(@IdRes id: Int): View? = rootView?.findViewById(id)
}