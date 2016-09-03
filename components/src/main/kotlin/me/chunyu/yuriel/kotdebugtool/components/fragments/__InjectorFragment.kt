package me.chunyu.yuriel.kotdebugtool.components.fragments

import android.os.Bundle
import android.support.annotation.IdRes
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import me.chunyu.yuriel.kotdebugtool.components.R
import me.chunyu.yuriel.kotdebugtool.components.SearchableListViewHelper
import me.chunyu.yuriel.kotdebugtool.components.injector.AllActivities
import me.chunyu.yuriel.kotdebugtool.components.injector.InjectableAdapter
import me.chunyu.yuriel.kotdebugtool.components.injector.IntentInjector
import me.chunyu.yuriel.kotdebugtool.components.injector.RunnableInjector

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
        arguments.getInt(TYPE, TYPE_ALL_ACTIVITIES)
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
        val result = findViewById(R.id.edit_text) as EditText
        result.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val pattern = SearchableListViewHelper.getPattern(s.toString())
                adapter.highlight(pattern, s.toString())
            }
        })
        result
    }

    private val list by lazy {
        val result = findViewById(R.id.list_view) as ListView
        result.adapter = adapter
        result.onItemClickListener = adapter
        result
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.__activity_injector, container, false)
        this.rootView = rootView
        list; editText
        return rootView
    }

    private fun findViewById(@IdRes id: Int): View? {
        return rootView?.findViewById(id)
    }


}