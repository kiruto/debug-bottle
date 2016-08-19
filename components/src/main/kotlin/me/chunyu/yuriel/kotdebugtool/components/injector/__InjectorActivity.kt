package me.chunyu.yuriel.kotdebugtool.components.injector

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ListView
import me.chunyu.yuriel.kotdebugtool.components.R
import me.chunyu.yuriel.kotdebugtool.components.SearchableListViewHelper
import me.chunyu.yuriel.kotdebugtool.components.injector.*

class __InjectorActivity : AppCompatActivity() {

    companion object {
        val TYPE = "type"
        val TYPE_RUNNABLE = 0x11
        val TYPE_INTENT = 0x22
        val TYPE_ALL_ACTIVITIES = 0x33
    }

    private val type by lazy { intent.extras.getInt(TYPE) }

    private val adapter by lazy {
        val injectable = when (type) {
            TYPE_RUNNABLE -> RunnableInjector
            TYPE_INTENT -> {
                IntentInjector.setActivity(this)
                IntentInjector
            }
            TYPE_ALL_ACTIVITIES -> AllActivities(this)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.__activity_injector)
        list
        editText
    }
}
