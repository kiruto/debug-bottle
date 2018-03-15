package com.exyui.android.debugbottle.components.okhttp

import android.app.Activity
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import com.exyui.android.debugbottle.components.R
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by yuriel on 8/24/16.
 */
internal class DisplayHttpBlockActivity : Activity() {

    companion object {

        private val TAG = "__DisplayBlockActivity"
        private val SHOW_BLOCK_EXTRA = "show_latest"
        val SHOW_BLOCK_EXTRA_KEY = "BlockStartTime"

        @JvmOverloads fun createPendingIntent(context: Context, blockStartTime: String? = null): PendingIntent {
            val intent = Intent(context, DisplayHttpBlockActivity::class.java)
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

    private val mBlockEntries: MutableList<HttpBlock> by lazy { ArrayList<HttpBlock>() }
    private var mBlockStartTime: String? = null

    private val mListView by lazy { findViewById<ListView>(R.id.__dt_canary_display_leak_list)!! }
    private val mFailureView by lazy { findViewById<TextView>(R.id.__dt_canary_display_leak_failure)!! }
    private val mActionButton by lazy { findViewById<Button>(R.id.__dt_canary_action)!! }
    private val mMaxStoredBlockCount by lazy { resources.getInteger(R.integer.__block_canary_max_stored_count) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityManager.isUserAMonkey()) {
            finish()
        }

        if (savedInstanceState != null) {
            mBlockStartTime = savedInstanceState.getString(SHOW_BLOCK_EXTRA_KEY)
        } else {
            val intent = intent
            if (intent.hasExtra(SHOW_BLOCK_EXTRA)) {
                mBlockStartTime = intent.getStringExtra(SHOW_BLOCK_EXTRA)
            }
        }
        setContentView(R.layout.__dt_canary_display_leak)
        updateUi()
    }

    // No, it's not deprecated. Android lies.
    override fun onRetainNonConfigurationInstance(): Any {
        return mBlockEntries
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SHOW_BLOCK_EXTRA_KEY, mBlockStartTime)
    }

    override fun onResume() {
        super.onResume()
        LoadBlocks.load(this)
    }

    override fun setTheme(resid: Int) {
        // We don't want this to be called with an incompatible theme.
        // This could happen if you implement runtime switching of themes
        // using ActivityLifecycleCallbacks.
        if (resid != R.style.__dt_canary_BlockCanary_Base) {
            return
        }
        super.setTheme(resid)
    }

    override fun onDestroy() {
        super.onDestroy()
        LoadBlocks.forgetActivity()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            mBlockStartTime = null
            updateUi()
        }
        return true
    }

    override fun onBackPressed() {
        if (mBlockStartTime != null) {
            mBlockStartTime = null
            updateUi()
        } else {
            super.onBackPressed()
        }
    }

    private fun shareBlock(block: HttpBlock) {
        val leakInfo = block.toString()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, leakInfo)
        startActivity(Intent.createChooser(intent, getString(R.string.__block_canary_share_with)))
    }

    private fun shareHeapDump(block: HttpBlock) {
        val heapDumpFile = block.file

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
            mListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                mBlockStartTime = mBlockEntries[position].timeStart.toString()
                updateUi()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                invalidateOptionsMenu()
                val actionBar = actionBar
                actionBar?.setDisplayHomeAsUpEnabled(false)
            }
            title = "Http Listener"
            mActionButton.setText(R.string.__block_canary_delete_all)
            mActionButton.setOnClickListener {
                HttpBlockFileMgr.deleteLogFiles()
                mBlockEntries.clear()
                updateUi()
            }
        }
        mActionButton.visibility = if (mBlockEntries.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun renderBlockDetail(block: HttpBlock?) {
        val listAdapter = mListView.adapter
        val adapter: HttpBlockDetailAdapter
        if (listAdapter is HttpBlockDetailAdapter) {
            adapter = listAdapter
        } else {
            adapter = HttpBlockDetailAdapter()
            mListView.adapter = adapter
            mListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                adapter.toggleRow(position)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                invalidateOptionsMenu()
                val actionBar = actionBar
                actionBar?.setDisplayHomeAsUpEnabled(true)
            }
            mActionButton.visibility = View.VISIBLE
            mActionButton.setText(R.string.__block_canary_delete)
            mActionButton.setOnClickListener {
                if (block != null) {
                    block.file?.delete()
                    mBlockStartTime = null
                    mBlockEntries.remove(block)
                    updateUi()
                }
            }
        }
        adapter.update(block)
        title = "${block?.method}: ${block?.url}"
    }

    private fun getBlock(startTime: String?): HttpBlock? {
        if (TextUtils.isEmpty(startTime)) {
            return null
        }
        for (block in mBlockEntries) {
            if (block.timeStart.toString() == startTime) {
                return block
            }
        }
        return null
    }

    internal inner class BlockListAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return mBlockEntries.size
        }

        override fun getItem(position: Int): HttpBlock {
            return mBlockEntries[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (view == null) {
                view = LayoutInflater.from(this@DisplayHttpBlockActivity).inflate(R.layout.__dt_canary_block_row, parent, false)
            }
            val titleView = view!!.findViewById<TextView>(R.id.__dt_canary_row_text)
            val timeView = view.findViewById<TextView>(R.id.__dt_canary_row_time)
            val block = getItem(position)

            val index: String
            if (position == 0 && mBlockEntries.size == mMaxStoredBlockCount) {
                index = "MAX. "
            } else {
                index = "${mBlockEntries.size - position}. "
            }

            val title = index + block.method + " " + block.url
            titleView.text = title
            timeView.text = String.format("%.1f", block.time) + "ms"
            return view
        }
    }

    internal class LoadBlocks(private var activityOrNull: DisplayHttpBlockActivity?) : Runnable {
        private val mainHandler: Handler

        init {
            mainHandler = Handler(Looper.getMainLooper())
        }

        override fun run() {
            val blocks = ArrayList<HttpBlock>()
            val files = HttpBlockFileMgr.logFiles
            if (files != null) {
                for (blockFile in files) {
                    try {
                        blocks.add(HttpBlock.newInstance(blockFile))
                    } catch (e: Exception) {
                        // Likely a format change in the blockFile
                        blockFile.delete()
                        Log.e(TAG, "Could not read block log file, deleted :" + blockFile, e)
                    }

                }
                Collections.sort(blocks) { lhs, rhs ->
                    rhs.file?.lastModified()?.compareTo((lhs.file?.lastModified())?: 0L)?: 0
                }
            }
            mainHandler.post {
                inFlight.remove(this@LoadBlocks)
                if (activityOrNull != null) {
                    activityOrNull!!.mBlockEntries.clear()
                    activityOrNull!!.mBlockEntries.addAll(blocks)
                    //Log.d("BlockCanary", "load block entries: " + blocks.size());
                    activityOrNull!!.updateUi()
                }
            }
        }

        companion object {

            val inFlight: MutableList<LoadBlocks> = ArrayList()
            val backgroundExecutor: Executor = Executors.newSingleThreadExecutor()

            fun load(activity: DisplayHttpBlockActivity) {
                val loadBlocks = LoadBlocks(activity)
                inFlight.add(loadBlocks)
                backgroundExecutor.execute(loadBlocks)
            }

            fun forgetActivity() {
                for (loadBlocks in inFlight) {
                    loadBlocks.activityOrNull = null
                }
                inFlight.clear()
            }
        }
    }
}