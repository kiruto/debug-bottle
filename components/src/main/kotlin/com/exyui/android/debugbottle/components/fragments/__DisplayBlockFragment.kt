package com.exyui.android.debugbottle.components.fragments

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.IdRes
import android.text.Html
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import android.view.*
import android.widget.*
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.core.__CanaryCoreMgr
import com.exyui.android.debugbottle.core.__LogWriter
import com.exyui.android.debugbottle.core.log.__Block
import com.exyui.android.debugbottle.core.log.__BlockCanaryInternals
import com.exyui.android.debugbottle.core.log.__ProcessUtils
import com.exyui.android.debugbottle.ui.layout.__DisplayBlockActivity
import com.exyui.android.debugbottle.views.__DisplayLeakConnectorView
import com.exyui.android.debugbottle.views.__MoreDetailsView
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by yuriel on 9/3/16.
 */
class __DisplayBlockFragment: __ContentFragment() {
    companion object {

        private val TAG = "__DisplayBlockActivity"
        private val SHOW_BLOCK_EXTRA = "show_latest"
        val SHOW_BLOCK_EXTRA_KEY = "BlockStartTime"

        @JvmOverloads fun createPendingIntent(context: Context, blockStartTime: String? = null): PendingIntent {
            val intent = Intent(context, __DisplayBlockActivity::class.java)
            intent.putExtra(SHOW_BLOCK_EXTRA, blockStartTime)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        internal fun classSimpleName(className: String): String {
            val separator = className.lastIndexOf('.')
            if (separator == -1) {
                return className
            } else {
                return className.substring(separator + 1)
            }
        }
    }

    private var rootView: ViewGroup? = null
    private val mBlockEntries: MutableList<__Block> by lazy { ArrayList<__Block>() }
    private var mBlockStartTime: String? = null

    private val mListView by lazy { findViewById(R.id.__dt_canary_display_leak_list) as ListView }
    private val mFailureView by lazy { findViewById(R.id.__dt_canary_display_leak_failure) as TextView }
    private val mActionButton by lazy { findViewById(R.id.__dt_canary_action) as Button }
    private val mMaxStoredBlockCount by lazy { resources.getInteger(R.integer.__block_canary_max_stored_count) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.__dt_canary_display_leak_light, container, false)
        this.rootView = rootView as ViewGroup
        if (savedInstanceState != null) {
            mBlockStartTime = savedInstanceState.getString(SHOW_BLOCK_EXTRA_KEY)
        } else {
            val intent = context?.intent
            if (intent?.hasExtra(SHOW_BLOCK_EXTRA)?: false) {
                mBlockStartTime = intent?.getStringExtra(SHOW_BLOCK_EXTRA)
            }
        }
        setHasOptionsMenu(true)
        updateUi()
        return rootView
    }

    // No, it's not deprecated. Android lies.
//    override fun onRetainNonConfigurationInstance(): Any {
//        return mBlockEntries as Any
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SHOW_BLOCK_EXTRA_KEY, mBlockStartTime)
    }

    override fun onResume() {
        super.onResume()
        LoadBlocks.load(this)
    }

//    override fun setTheme(resid: Int) {
//        // We don't want this to be called with an incompatible theme.
//        // This could happen if you implement runtime switching of themes
//        // using ActivityLifecycleCallbacks.
//        if (resid != R.style.__dt_canary_BlockCanary_Base) {
//            return
//        }
//        super.setTheme(resid)
//    }

    override fun onDestroy() {
        super.onDestroy()
        LoadBlocks.forgetActivity()
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        val block = getBlock(mBlockStartTime)
        if (block != null) {
            menu.add(R.string.__block_canary_share_leak).setOnMenuItemClickListener {
                shareBlock(block)
                true
            }
            menu.add(R.string.__block_canary_share_stack_dump).setOnMenuItemClickListener {
                shareHeapDump(block)
                true
            }
            //return true
        }
        //return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            mBlockStartTime = null
            updateUi()
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        if (mBlockStartTime != null) {
            mBlockStartTime = null
            updateUi()
            return true
        } else {
            return super.onBackPressed()
        }
    }

    private fun shareBlock(block: __Block) {
        val leakInfo = block.toString()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, leakInfo)
        startActivity(Intent.createChooser(intent, getString(R.string.__block_canary_share_with)))
    }

    private fun shareHeapDump(block: __Block) {
        val heapDumpFile = block.logFile

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            heapDumpFile?.setReadable(true, false)
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/octet-stream"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(heapDumpFile))
        startActivity(Intent.createChooser(intent, getString(R.string.__block_canary_share_with)))
    }

    private fun updateUi() {
        val block = getBlock(mBlockStartTime)
        if (block == null) {
            mBlockStartTime = null
        }

        // Reset to defaults
        mListView.visibility = View.VISIBLE
        mFailureView.visibility = View.GONE

        if (block != null) {
            renderBlockDetail(block)
        } else {
            renderBlockList()
        }
    }

    private fun renderBlockList() {
        val listAdapter = mListView.adapter
        if (listAdapter is BlockListAdapter) {
            listAdapter.notifyDataSetChanged()
        } else {
            val adapter = BlockListAdapter()
            mListView.adapter = adapter
            mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                mBlockStartTime = mBlockEntries[position].timeStart
                updateUi()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                context?.invalidateOptionsMenu()
                //val actionBar = actionBar
                //actionBar?.setDisplayHomeAsUpEnabled(false)
            }
            //title = getString(R.string.__block_canary_block_list_title, packageName)
            mActionButton.setText(R.string.__block_canary_delete_all)
            mActionButton.setOnClickListener {
                __LogWriter.deleteLogFiles()
                mBlockEntries.clear()
                updateUi()
            }
        }
        mActionButton.visibility = if (mBlockEntries.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun renderBlockDetail(block: __Block?) {
        val listAdapter = mListView.adapter
        val adapter: __BlockDetailAdapter
        if (listAdapter is __BlockDetailAdapter) {
            adapter = listAdapter
        } else {
            adapter = __BlockDetailAdapter()
            mListView.adapter = adapter
            mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> adapter.toggleRow(position) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                context?.invalidateOptionsMenu()
                //val actionBar = actionBar
                //actionBar?.setDisplayHomeAsUpEnabled(true)
            }
            mActionButton.visibility = View.VISIBLE
            mActionButton.setText(R.string.__block_canary_delete)
            mActionButton.setOnClickListener {
                if (block != null) {
                    block.logFile?.delete()
                    mBlockStartTime = null
                    mBlockEntries.remove(block)
                    updateUi()
                }
            }
        }
        adapter.update(block)
        //title = getString(R.string.__block_canary_class_has_blocked, block!!.timeCost)
    }

    private fun getBlock(startTime: String?): __Block? {
        if (TextUtils.isEmpty(startTime)) {
            return null
        }
        for (block in mBlockEntries) {
            if (block.timeStart.equals(startTime)) {
                return block
            }
        }
        return null
    }

    private fun findViewById(@IdRes id: Int): View? {
        return rootView?.findViewById(id)
    }

    internal inner class BlockListAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return mBlockEntries.size
        }

        override fun getItem(position: Int): __Block {
            return mBlockEntries[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.__dt_canary_block_row, parent, false)
            }
            val titleView = view!!.findViewById(R.id.__dt_canary_row_text) as TextView
            val timeView = view.findViewById(R.id.__dt_canary_row_time) as TextView
            val block = getItem(position)

            val index: String
            if (position == 0 && mBlockEntries.size == mMaxStoredBlockCount) {
                index = "MAX. "
            } else {
                index = "${mBlockEntries.size - position}. "
            }

            val title = index + block.keyStackString + " " +
                    getString(R.string.__block_canary_class_has_blocked, block.timeCost)
            titleView.text = title
            val time = DateUtils.formatDateTime(context,
                    block.logFile?.lastModified()?: 0L,
                    DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE)
            timeView.text = time
            return view
        }
    }

    internal class LoadBlocks(private var fragmentOrNull: __DisplayBlockFragment?) : Runnable {
        private val mainHandler: Handler

        init {
            mainHandler = Handler(Looper.getMainLooper())
        }

        override fun run() {
            val blocks = ArrayList<__Block>()
            val files = __BlockCanaryInternals.logFiles
            if (files != null) {
                for (blockFile in files) {
                    try {
                        blocks.add(__Block.newInstance(blockFile))
                    } catch (e: Exception) {
                        // Likely a format change in the blockFile
                        blockFile.delete()
                        Log.e(TAG, "Could not read block log file, deleted :" + blockFile, e)
                    }

                }
                Collections.sort(blocks) { lhs, rhs ->
                    rhs.logFile?.lastModified()?.compareTo((lhs.logFile?.lastModified())?: 0L)?: 0
                }
            }
            mainHandler.post {
                inFlight.remove(this@LoadBlocks)
                if (fragmentOrNull != null) {
                    fragmentOrNull!!.mBlockEntries.clear()
                    fragmentOrNull!!.mBlockEntries.addAll(blocks)
                    //Log.d("BlockCanary", "load block entries: " + blocks.size());
                    fragmentOrNull!!.updateUi()
                }
            }
        }

        companion object {

            val inFlight: MutableList<LoadBlocks> = ArrayList()
            val backgroundExecutor: Executor = Executors.newSingleThreadExecutor()

            fun load(activity: __DisplayBlockFragment) {
                val loadBlocks = LoadBlocks(activity)
                inFlight.add(loadBlocks)
                backgroundExecutor.execute(loadBlocks)
            }

            fun forgetActivity() {
                for (loadBlocks in inFlight) {
                    loadBlocks.fragmentOrNull = null
                }
                inFlight.clear()
            }
        }
    }

    class __BlockDetailAdapter : BaseAdapter() {

        private var mFoldings = BooleanArray(0)

        private var mStackFoldPrefix: String? = null
        private var mBlock: __Block? = null

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

                val isThreadStackEntry = position == POSITION_THREAD_STACK + 1
                val element = getItem(position)
                var htmlString = elementToHtmlString(element, position, mFoldings[position])
                if (isThreadStackEntry && !mFoldings[position]) {
                    htmlString += " <font color='#919191'>" + "blocked" + "</font>"
                }
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
            var htmlString = element?.replace(__Block.SEPARATOR.toRegex(), "<br>")

            when (position) {
                POSITION_BASIC -> {
                    if (folding) {
                        htmlString = htmlString?.substring(htmlString.indexOf(__Block.KEY_CPU_CORE))
                    }
                    htmlString = String.format("<font color='#c48a47'>%s</font> ", htmlString)
                }
                POSITION_TIME -> {
                    if (folding) {
                        htmlString = htmlString?.substring(0, htmlString.indexOf(__Block.KEY_TIME_COST_START))
                    }
                    htmlString = String.format("<font color='#f3cf83'>%s</font> ", htmlString)
                }
                POSITION_CPU -> {
                    // FIXME Figure out why sometimes \r\n cannot replace completely
                    htmlString = element
                    if (folding) {
                        htmlString = htmlString?.substring(0, htmlString.indexOf(__Block.KEY_CPU_RATE))
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
                    htmlString = String.format("<font color='#000000'>%s</font> ", htmlString)
                }
            }
            return htmlString
        }

        fun update(block: __Block?) {
            if (mBlock != null && block?.timeStart.equals(mBlock!!.timeStart)) {
                // Same data, nothing to change.
                return
            }
            mBlock = block
            mFoldings = BooleanArray((mBlock!!.threadStackEntries?.size?: 0) + POSITION_THREAD_STACK)
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
            return (mBlock!!.threadStackEntries?.size?: 0) + POSITION_THREAD_STACK
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
                    val prefix = __CanaryCoreMgr.context?.stackFoldPrefix
                    mStackFoldPrefix = prefix?: __ProcessUtils.myProcessName()
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

}
