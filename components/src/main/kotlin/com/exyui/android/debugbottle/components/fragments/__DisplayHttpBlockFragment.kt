package com.exyui.android.debugbottle.components.fragments

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.IdRes
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.okhttp.HttpBlock
import com.exyui.android.debugbottle.components.okhttp.HttpBlockDetailAdapter
import com.exyui.android.debugbottle.components.okhttp.HttpBlockFileMgr
import com.exyui.android.debugbottle.components.okhttp.DisplayHttpBlockActivity
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by yuriel on 9/3/16.
 */
class __DisplayHttpBlockFragment: __ContentFragment() {
    private var rootView: ViewGroup? = null
    private val mBlockEntries: MutableList<HttpBlock> by lazy { ArrayList<HttpBlock>() }
    private var mBlockStartTime: String? = null

    private val mListView by lazy { findViewById(R.id.__dt_canary_display_leak_list) as ListView }
    private val mFailureView by lazy { findViewById(R.id.__dt_canary_display_leak_failure) as TextView }
    private val mActionButton by lazy { findViewById(R.id.__dt_canary_action) as Button }
    private val mMaxStoredBlockCount by lazy { resources.getInteger(R.integer.__block_canary_max_stored_count) }

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
            return if (separator == -1) {
                className
            } else {
                className.substring(separator + 1)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        if (savedInstanceState != null) {
            mBlockStartTime = savedInstanceState.getString(SHOW_BLOCK_EXTRA_KEY)
        } else {
            val intent = context?.intent
            if (intent?.hasExtra(SHOW_BLOCK_EXTRA) == true) {
                mBlockStartTime = intent.getStringExtra(SHOW_BLOCK_EXTRA)
            }
        }

        val rootView = inflater.inflate(R.layout.__dt_canary_display_leak_light, container, false)
        this.rootView = rootView as ViewGroup
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
        return if (mBlockStartTime != null) {
            mBlockStartTime = null
            updateUi()
            true
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
                context?.invalidateOptionsMenu()
                //val actionBar = actionBar
                //actionBar?.setDisplayHomeAsUpEnabled(false)
            }
            //title = "Http Listener"
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
                context?.invalidateOptionsMenu()
                //val actionBar = actionBar
                //actionBar?.setDisplayHomeAsUpEnabled(true)
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
        //title = "${block?.method}: ${block?.url}"
    }

    private fun getBlock(startTime: String?): HttpBlock? {
        if (TextUtils.isEmpty(startTime)) {
            return null
        }
        return mBlockEntries.firstOrNull { it.timeStart.toString() == startTime }
    }

    private fun findViewById(@IdRes id: Int): View? = rootView?.findViewById(id)

    internal inner class BlockListAdapter : BaseAdapter() {

        override fun getCount(): Int = mBlockEntries.size

        override fun getItem(position: Int): HttpBlock = mBlockEntries[position]

        override fun getItemId(position: Int): Long = position.toLong()

        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNUSED_VALUE")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.__dt_canary_block_row, parent, false)
            }
            val titleView = view!!.findViewById<TextView>(R.id.__dt_canary_row_text)
            val timeView = view.findViewById<TextView>(R.id.__dt_canary_row_time)
            val block = getItem(position)

            val index: String = if (position == 0 && mBlockEntries.size == mMaxStoredBlockCount) {
                "MAX. "
            } else {
                "${mBlockEntries.size - position}. "
            }

            val title = "${block.responseCode}. ${block.method} ${block.url.split("?")[0].replace("http://", "").replace("https://", "")}"
            titleView.text = title
            timeView.text = "${block.time}ms"

            if (block.responseCode.startsWith("2")) {
                titleView.setTextColor(Color.parseColor("#DE1B5E20"))
            } else if (block.responseCode.startsWith("4") || block.responseCode.startsWith("5")) {
                titleView.setTextColor(Color.parseColor("#DED50000"))
            } else if (block.responseCode.startsWith("3")) {
                titleView.setTextColor(Color.parseColor("#DEFF6D00"))
            }

            return view
        }
    }

    internal class LoadBlocks(private var fragmentOrNull: __DisplayHttpBlockFragment?) : Runnable {
        private val mainHandler: Handler = Handler(Looper.getMainLooper())

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
            private val backgroundExecutor: Executor = Executors.newSingleThreadExecutor()

            fun load(activity: __DisplayHttpBlockFragment) {
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
}