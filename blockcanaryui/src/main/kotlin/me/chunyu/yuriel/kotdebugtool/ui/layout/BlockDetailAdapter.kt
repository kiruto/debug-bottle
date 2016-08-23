package me.chunyu.yuriel.kotdebugtool.ui.layout

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.chunyu.yuriel.kotdebugtool.core.CanaryCoreMgr
import me.chunyu.yuriel.kotdebugtool.core.log.Block
import me.chunyu.yuriel.kotdebugtool.core.log.ProcessUtils
import me.chunyu.yuriel.kotdebugtool.ui.R
import java.util.*

/**
 * Created by yuriel on 8/9/16.
 */
internal class BlockDetailAdapter : BaseAdapter() {

    private var mFoldings = BooleanArray(0)

    private var mStackFoldPrefix: String? = null
    private var mBlock: Block? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView
        val context = parent.context
        if (getItemViewType(position) == TOP_ROW) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.__block_canary_ref_top_row, parent, false)
            }
            val textView = view!!.findViewById(R.id.__leak_canary_row_text) as TextView
            textView.text = context.packageName
        } else {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.__block_canary_ref_row, parent, false)
            }
            val textView = view!!.findViewById(R.id.__leak_canary_row_text) as TextView

            val isThreadStackEntry = position == POSITION_THREAD_STACK + 1
            val element = getItem(position)
            var htmlString = elementToHtmlString(element, position, mFoldings[position])
            if (isThreadStackEntry && !mFoldings[position]) {
                htmlString += " <font color='#919191'>" + "blocked" + "</font>"
            }
            textView.text = Html.fromHtml(htmlString)

            val connectorView = view.findViewById(R.id.__leak_canary_row_connector) as __DisplayLeakConnectorView
            connectorView.setType(connectorViewType(position))

            val moreDetailsView = view.findViewById(R.id.__leak_canary_row_more) as __MoreDetailsView
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
        var htmlString = element?.replace(Block.SEPARATOR.toRegex(), "<br>")

        when (position) {
            POSITION_BASIC -> {
                if (folding) {
                    htmlString = htmlString?.substring(htmlString.indexOf(Block.KEY_CPU_CORE))
                }
                htmlString = String.format("<font color='#c48a47'>%s</font> ", htmlString)
            }
            POSITION_TIME -> {
                if (folding) {
                    htmlString = htmlString?.substring(0, htmlString.indexOf(Block.KEY_TIME_COST_START))
                }
                htmlString = String.format("<font color='#f3cf83'>%s</font> ", htmlString)
            }
            POSITION_CPU -> {
                // FIXME Figure out why sometimes \r\n cannot replace completely
                htmlString = element
                if (folding) {
                    htmlString = htmlString?.substring(0, htmlString.indexOf(Block.KEY_CPU_RATE))
                }
                htmlString = htmlString?.replace("cpurate = ", "<br>cpurate<br/>")
                htmlString = String.format("<font color='#998bb5'>%s</font> ", htmlString)
                htmlString = htmlString.replace("]".toRegex(), "]<br>")
            }
            //POSITION_THREAD_STACK,
            else -> {
                if (folding) {
                    val index: Int = htmlString?.indexOf(stackFoldPrefix)?: 0
                    if (index > 0) {
                        htmlString = htmlString?.substring(index)
                    }
                }
                htmlString = String.format("<font color='#ffffff'>%s</font> ", htmlString)
            }
        }
        return htmlString
    }

    fun update(block: Block?) {
        if (mBlock != null && block?.timeStart.equals(mBlock!!.timeStart)) {
            // Same data, nothing to change.
            return
        }
        mBlock = block
        mFoldings = BooleanArray(mBlock!!.threadStackEntries?.size?: 0 + POSITION_THREAD_STACK)
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
        return mBlock!!.threadStackEntries?.size?: 0 + POSITION_THREAD_STACK
    }

    override fun getItem(position: Int): String? {
        if (getItemViewType(position) == TOP_ROW) {
            return null
        }
        when (position) {
            POSITION_BASIC -> return mBlock?.basicString
            POSITION_TIME -> return mBlock?.timeString
            POSITION_CPU -> return mBlock?.cpuString
            //POSITION_THREAD_STACK,
            else -> return mBlock?.threadStackEntries?.get(position - POSITION_THREAD_STACK)
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

    private val stackFoldPrefix: String
        get() {
            if (mStackFoldPrefix == null) {
                val prefix = CanaryCoreMgr.context?.stackFoldPrefix
                mStackFoldPrefix = prefix?: ProcessUtils.myProcessName()
            }
            return mStackFoldPrefix!!
        }

    companion object {

        private val TOP_ROW = 0
        private val NORMAL_ROW = 1

        private val POSITION_BASIC = 1
        private val POSITION_TIME = 2
        private val POSITION_CPU = 3
        private val POSITION_THREAD_STACK = 4
    }
}
