package me.chunyu.yuriel.kotdebugtool.components.injector

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import me.chunyu.yuriel.kotdebugtool.components.R
import me.chunyu.yuriel.kotdebugtool.components.SearchableListViewHelper

/**
 * Created by yuriel on 8/15/16.
 */
internal class InjectableAdapter<out T>(private val injectable: Injectable<T>):
        BaseAdapter(), AdapterView.OnItemClickListener,
        SearchableListViewHelper.HighlightListener<String> {

    @Volatile private var matchedMap: Map<String, List<String>>? = null
    @Volatile private var filterString: String = ""

    override val collections: Collection<String> = injectable.model.keys

    override fun getCount(): Int {
        if (matchedMap?.isEmpty()?: true) return injectable.model.size
        return matchedMap?.size?: 0
    }

    override fun update(map: Map<String, List<String>>, str: String) {
        matchedMap = map
        filterString = str
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val context = parent?.context?: return null
        val v: View = convertView?:
                LayoutInflater.from(context).inflate(R.layout.__item_injector, parent, false)
        val h: Holder = if (null != v.tag) v.tag as Holder else {
            val result = Holder()
            result.textView = v.findViewById(R.id.text_view) as TextView
            v.tag = result
            result
        }
        val item = getItem(position)
        if (!filterString.isEmpty() && matchedMap?.isEmpty()?: true)
            h.textView?.text = item?.first
        else {
            val key = item?.first?: ""
            val builder = SpannableStringBuilder(key)
            var targetString = filterString
            for (char in key) {
                if (targetString.isEmpty()) break
                val index = key.indexOf(char)
                if (char.toUpperCase() == targetString.first().toUpperCase()) {
                    val span = BackgroundColorSpan(Color.parseColor("#FFEB3B"))
                    val bold = StyleSpan(Typeface.BOLD)
                    builder.setSpan(span, index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    builder.setSpan(bold, index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    targetString = targetString.removeRange(0..0)
                }
            }
            h.textView?.text = builder
        }

        return v
    }

    override fun getItem(position: Int): Pair<String, T?>? {
        val keys = injectable.model.entries
        var i = 0
        if (matchedMap?.isEmpty()?: true) {
            for ((k, v) in keys) {
                if (i++ == position) {
                    return Pair(k, v)
                }
            }
        } else {
            for ((k, v) in matchedMap!!) {
                if (i++ == position) {
                    return Pair(getString(k), injectable.model[getString(k)])
                }
            }
        }
        return null
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val key = getItem(position)?.first
        if (null == key) return else
        injectable.run(key)
    }

    override fun getString(t: String) = t

    class Holder {
        var textView: TextView? = null
    }
}