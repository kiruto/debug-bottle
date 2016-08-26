package me.chunyu.yuriel.kotdebugtool.components.okhttp

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.chunyu.yuriel.kotdebugtool.components.R
import me.chunyu.yuriel.kotdebugtool.views.__DisplayLeakConnectorView
import me.chunyu.yuriel.kotdebugtool.views.__MoreDetailsView
import java.util.*

/**
 * Created by yuriel on 8/25/16.
 */
internal class HttpBlockDetailAdapter: BaseAdapter() {

    private var mFoldings = BooleanArray(0)

    private var mBlock: HttpBlock? = null

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
            val htmlString = elementToHtmlString(element, position, mFoldings[position])
            textView.text = Html.fromHtml(htmlString)

            val connectorView = view.findViewById(R.id.__dt_canary_row_connector) as __DisplayLeakConnectorView
            connectorView.setType(connectorViewType(position))

            val moreDetailsView = view.findViewById(R.id.__dt_canary_row_more) as __MoreDetailsView
            moreDetailsView.setFolding(mFoldings[position])
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

    private fun elementToHtmlString(element: String?, position: Int, folding: Boolean): String {
        var htmlString = element?.replace(HttpBlock.SEPARATOR.toRegex(), "<br>")

        when (position) {
            POSITION_OVERVIEW -> {
                htmlString = String.format("<font color='#c48a47'>%s</font> ", htmlString)
            }
            POSITION_REQUEST -> {
                htmlString = String.format("<font color='#f3cf83'>%s</font> ", htmlString)
            }
            POSITION_RESPONSE -> {
                htmlString = String.format("<font color='#998bb5'>%s</font> ", htmlString)
            }
            else -> {
                htmlString = String.format("<font color='#ffffff'>%s</font> ", htmlString)
            }
        }
        return htmlString
    }

    fun update(block: HttpBlock?) {
        if (mBlock != null && block!!.timeStart.equals(mBlock!!.timeStart)) {
            // Same data, nothing to change.
            return
        }
        mBlock = block
        mFoldings = BooleanArray(POSITION_RESPONSE)
        Arrays.fill(mFoldings, true)
        notifyDataSetChanged()
    }

    fun toggleRow(position: Int) {
        mFoldings[position] = !mFoldings[position]
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        if (mBlock == null) {
            return 0
        }
        return POSITION_RESPONSE
    }

    override fun getItem(position: Int): String? {
        if (getItemViewType(position) == TOP_ROW) {
            return null
        }
        when (position) {
            POSITION_OVERVIEW -> return mBlock?.overviewString
            POSITION_REQUEST -> return mBlock?.requestString
            POSITION_RESPONSE -> return mBlock?.responseString
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

        private val POSITION_OVERVIEW = 1
        private val POSITION_REQUEST = 2
        private val POSITION_RESPONSE = 3
    }
}