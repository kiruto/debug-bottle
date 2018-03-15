package com.exyui.android.debugbottle.components.crash

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.views.__DisplayLeakConnectorView
import com.exyui.android.debugbottle.views.__MoreDetailsView
import java.util.*

/**
 * Created by yuriel on 9/13/16.
 */
internal class CrashBlockDetailAdapter: BaseAdapter() {
    private var mFoldings = BooleanArray(0)

    private var mBlock: CrashBlock? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView
        val context = parent.context
        if (getItemViewType(position) == TOP_ROW) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.__dt_canary_ref_top_row, parent, false)
            }
            val textView = view!!.findViewById<TextView>(R.id.__dt_canary_row_text)
            textView.text = context.packageName
        } else {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.__dt_canary_ref_row, parent, false)
            }
            val textView = view!!.findViewById<TextView>(R.id.__dt_canary_row_text)

            val element = getItem(position)
            val htmlString = elementToHtmlString(element, position, mFoldings[position - 1])
            textView.text = Html.fromHtml(htmlString)

            val connectorView = view.findViewById<__DisplayLeakConnectorView>(R.id.__dt_canary_row_connector)
            connectorView.setType(connectorViewType(position))

            val moreDetailsView = view.findViewById<__MoreDetailsView>(R.id.__dt_canary_row_more)
            moreDetailsView.setFolding(mFoldings[position - 1])
        }

        return view
    }

    private fun connectorViewType(position: Int): __DisplayLeakConnectorView.Type =
            when (position) {
                1 -> __DisplayLeakConnectorView.Type.START
                count - 1 -> __DisplayLeakConnectorView.Type.END
                else -> __DisplayLeakConnectorView.Type.NODE
            }

    @Suppress("UNUSED_PARAMETER")
    private fun elementToHtmlString(element: String?, position: Int, folding: Boolean): String {
        return element
                ?.replace(CrashBlock.SEPARATOR.toRegex(), "<br>")
                .let {
                    when (position) {
                        POSITION_THREAD -> "<font color='#c48a47'>$it</font> "
                        POSITION_STACKTRACE -> "<font color='#000000'>$it</font> "
                        else -> "<font color='#ffffff'>$it</font> "
                    }
                }
    }

    fun update(block: CrashBlock?) {
        if (mBlock != null && block?.time == mBlock?.time) {
            // Same data, nothing to change.
            return
        }
        mBlock = block
        mFoldings = BooleanArray(POSITION_STACKTRACE)
        Arrays.fill(mFoldings, true)
        notifyDataSetChanged()
    }

    fun toggleRow(position: Int) {
        if(position == 0) return
        mFoldings[position - 1] = !mFoldings[position - 1]
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        if (mBlock == null) {
            return 0
        }
        return POSITION_STACKTRACE + 1
    }

    override fun getItem(position: Int): String? {
        if (getItemViewType(position) == TOP_ROW) {
            return null
        }
        return when (position) {
            POSITION_THREAD -> mBlock?.threadString
            POSITION_STACKTRACE -> mBlock?.stacktraceString
            else -> ""
        }
    }

    override fun getViewTypeCount(): Int = 2

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TOP_ROW
        }
        return NORMAL_ROW
    }

    override fun getItemId(position: Int): Long = position.toLong()

    companion object {

        private const val TOP_ROW = 0
        private const val NORMAL_ROW = 1

        private const val POSITION_THREAD = 1
        private const val POSITION_STACKTRACE = 2
    }
}