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
            val textView = view!!.findViewById(R.id.__dt_canary_row_text) as TextView
            textView.text = context.packageName
        } else {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.__dt_canary_ref_row, parent, false)
            }
            val textView = view!!.findViewById(R.id.__dt_canary_row_text) as TextView

            val element = getItem(position)
            val htmlString = elementToHtmlString(element, position, mFoldings[position - 1])
            textView.text = Html.fromHtml(htmlString)

            val connectorView = view.findViewById(R.id.__dt_canary_row_connector) as __DisplayLeakConnectorView
            connectorView.setType(connectorViewType(position))

            val moreDetailsView = view.findViewById(R.id.__dt_canary_row_more) as __MoreDetailsView
            moreDetailsView.setFolding(mFoldings[position - 1])
        }

        return view
    }

    private fun connectorViewType(position: Int): __DisplayLeakConnectorView.Type {
        return if (position == 1)
            __DisplayLeakConnectorView.Type.START
        else
            if (position == count - 1)
                __DisplayLeakConnectorView.Type.END
            else
                __DisplayLeakConnectorView.Type.NODE
    }

    @Suppress("UNUSED_PARAMETER")
    private fun elementToHtmlString(element: String?, position: Int, folding: Boolean): String {
        var htmlString = element?.replace(CrashBlock.SEPARATOR.toRegex(), "<br>")

        when (position) {
            POSITION_THREAD -> {
                htmlString = String.format("<font color='#c48a47'>%s</font> ", htmlString)
            }
            POSITION_STACKTRACE -> {
                htmlString = String.format("<font color='#000000'>%s</font> ", htmlString)
            }
            else -> {
                htmlString = String.format("<font color='#ffffff'>%s</font> ", htmlString)
            }
        }
        return htmlString
    }

    fun update(block: CrashBlock?) {
        if (mBlock != null && block!!.time.equals(mBlock!!.time)) {
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
        when (position) {
            POSITION_THREAD -> return mBlock?.threadString
            POSITION_STACKTRACE -> return mBlock?.stacktraceString
            else -> return ""
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TOP_ROW
        }
        return NORMAL_ROW
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object {

        private val TOP_ROW = 0
        private val NORMAL_ROW = 1

        private val POSITION_THREAD = 1
        private val POSITION_STACKTRACE = 2
    }
}