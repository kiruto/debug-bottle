package com.exyui.android.debugbottle.components

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.*
import com.squareup.leakcanary.internal.DisplayLeakActivity
import com.exyui.android.debugbottle.components.fragments.*
import com.exyui.android.debugbottle.components.guide.IntroductionActivity

/**
 * Created by yuriel on 9/3/16.
 */
internal class DTDrawerActivity : AppCompatActivity(), DialogsCollection.SPDialogAction {

    private var contentFragment: __ContentFragment? = null

    private val titles: Array<String> by lazy {
        resources.getStringArray(R.array.__dt_drawer_items)
    }

    private val drawerLayout by lazy {
        val result = findViewById(R.id.__dt_drawer_layout) as DrawerLayout
        result
    }

    private val drawerRoot by lazy {
        val result = findViewById(R.id.__dt_drawer_root) as ViewGroup
        result
    }

    private val drawerListView by lazy {
        val result = findViewById(R.id.__dt_left_drawer) as ListView
        result.adapter = DrawerAdapter(titles)
        result.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            selectItem(position)
        }
        result
    }

    private val contentFrame by lazy {
        val result = findViewById(R.id.__dt_content_frame) as ViewGroup
        result
    }

    private val drawerToggle by lazy {
        val result = object: ActionBarDrawerToggle(this, drawerLayout, R.string.__dt_drawer_open, R.string.__dt_drawer_close) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }
        }
        result.setHomeAsUpIndicator(R.drawable.__dt_ic_bottle_24dp)
        result
    }

    private val infoLayout by lazy {
        val result = findViewById(R.id.__dt_info) as ViewGroup
        result.setOnClickListener {
            AlertDialog.Builder(this)
                    .setIcon(R.drawable.__dt_ic_bottle_24dp)
                    .setTitle(R.string.__dt_info)
                    .setMessage(R.string.__dt_info_introduction)
                    .setNegativeButton(R.string.__dt_close) { dialog, witch -> }
                    .setNeutralButton(R.string.__dt_github) { dialog, witch ->
                        val url = "https://github.com/kiruto/debug-bottle"
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                    .show()
        }
        result
    }

    private val introLayout by lazy {
        val result = findViewById(R.id.__dt_helper) as ViewGroup
        result.setOnClickListener {
            val intent = Intent(this, IntroductionActivity::class.java)
            val clazz = ContextThemeWrapper::class.java
            val method = clazz.getMethod("getThemeResId")
            method.isAccessible = true
            val themeResId = method.invoke(this) as Int
            intent.putExtra("theme", themeResId)
            startActivity(intent)
        }
        result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.__activity_dt_drawer)
        drawerLayout.addDrawerListener(drawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.__dt_ic_bottle_24dp)
        drawerListView; infoLayout; introLayout
        selectItem(0)
        drawerLayout.openDrawer(Gravity.LEFT)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        supportActionBar?.title = title
    }

    override fun onBackPressed() {
        if (!(contentFragment?.onBackPressed()?: false)) super.onBackPressed()
    }

    override fun updateSPViews() {
        val f = supportFragmentManager.findFragmentByTag(__SPViewerFragment.TAG) as __SPViewerFragment?
        f?.updateSPViews()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode > -1 && requestCode < __StatusFragment.permissions.size) {
            val permission = __StatusFragment.permissions[requestCode]
            updatePermissionStatus()
            return
        } else
            return
    }

    private fun updatePermissionStatus() {
        val f = supportFragmentManager.findFragmentByTag(__StatusFragment.TAG) as __StatusFragment?
        f?: return
        f.updatePermissionStatus()
    }

    private fun s(@IdRes id: Int): String = resources.getString(id)

    private fun selectItem(position: Int) {

        var fragment: __ContentFragment? = null

        when (titles[position]) {

            s(R.string.__dt_status) -> {
                fragment = __StatusFragment()
            }

            s(R.string.__dt_all_activities) -> {
                fragment = __InjectorFragment.newInstance(__InjectorFragment.TYPE_ALL_ACTIVITIES)
            }

            s(R.string.__dt_intents) -> {
                fragment =  __InjectorFragment.newInstance(__InjectorFragment.TYPE_INTENT)
            }

            s(R.string.__dt_runnable) -> {
                fragment = __InjectorFragment.newInstance(__InjectorFragment.TYPE_RUNNABLE)
            }

            s(R.string.__dt_sp_viewer) -> {
                fragment = __SPViewerFragment()
            }

            s(R.string.__dt_blocks) -> {
                fragment = __DisplayBlockFragment()
            }

            s(R.string.__dt_network_traffics) -> {
                fragment = __DisplayHttpBlockFragment()
            }

            s(R.string.__dt_leaks) -> {
                if (RunningFeatureMgr.has(RunningFeatureMgr.LEAK_CANARY)) {
                    val intent = Intent(this, DisplayLeakActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "You should enable leak canary first!", Toast.LENGTH_SHORT).show()
                }
                return
            }

            s(R.string.__dt_feedback) -> {
                fragment = __WebViewFragment.newInstance("https://github.com/kiruto/debug-bottle/issues")
            }

            s(R.string.__dt_project) -> {
                fragment = __WebViewFragment.newInstance("https://github.com/kiruto/debug-bottle")
            }

            s(R.string.__dt_settings) -> {
                fragment = __SettingsFragment()
            }

            else -> {

            }
        }

        if (null != fragment) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.__dt_content_frame, fragment, fragment.TAG)
                    .commit()
            contentFragment = fragment
        }

        drawerListView.setItemChecked(position, true)
        title = titles[position]
        drawerLayout.closeDrawer(drawerRoot)
    }

    internal inner class DrawerAdapter(val titles: Array<String>): BaseAdapter() {

        private val menu by lazy {
            val result = mutableListOf<DrawerMenuItem>()
            for (str in titles) {
                // Icon color is #8A000000
                result.add(DrawerMenuItem(str, when (str) {
                    s(R.string.__dt_status) -> R.drawable.__ic_home_black_24dp
                    s(R.string.__dt_all_activities) -> R.drawable.__ic_find_in_page_black_24dp
                    s(R.string.__dt_intents) -> R.drawable.__ic_android_black_24dp
                    s(R.string.__dt_runnable) -> R.drawable.__ic_clear_all_black_24dp
                    s(R.string.__dt_sp_viewer) -> R.drawable.__ic_edit_black_24dp
                    s(R.string.__dt_blocks) -> R.drawable.__ic_block_black_24dp
                    s(R.string.__dt_network_traffics) -> R.drawable.__ic_network_check_black_24dp
                    s(R.string.__dt_leaks) -> R.drawable.__ic_bug_report_black_24dp
                    s(R.string.__dt_settings) -> R.drawable.__ic_settings_black_24dp
                    s(R.string.__dt_crashes) -> R.drawable.__ic_report_problem_black_24dp
                    s(R.string.__dt_feedback) -> R.drawable.__ic_feedback_black_24dp
                    s(R.string.__dt_project) -> R.drawable.__ic_code_black_24dp
                    else -> R.drawable.__ic_info_outline_black_24dp
                }))
            }
            result
        }

        override fun getCount(): Int = titles.size

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var view = convertView
            val holder: ViewHolder
            val item = menu[position]
            if (null == view) {
                view = LayoutInflater.from(this@DTDrawerActivity).inflate(R.layout.__item_drawer_menu, parent, false)
                holder = ViewHolder()
                holder.icon = view.findViewById(R.id.__dt_icon) as ImageView
                holder.title = view.findViewById(R.id.__dt_item_title) as TextView
                view.tag = holder
            } else {
                holder = view.tag as ViewHolder
            }
            holder.icon?.setImageResource(item.icon)
            holder.title?.text = item.title
            return view
        }

        override fun getItem(position: Int): String = titles[position]

        override fun getItemId(position: Int): Long = position.toLong()
    }

    internal class ViewHolder {
        var icon: ImageView? = null
        var title: TextView? =  null
    }

    internal data class DrawerMenuItem(
            val title: String,
            @DrawableRes val icon: Int
    )
}