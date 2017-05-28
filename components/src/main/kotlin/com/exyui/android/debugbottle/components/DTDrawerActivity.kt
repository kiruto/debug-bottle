package com.exyui.android.debugbottle.components

import android.Manifest
import android.app.ActivityManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
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
 *
 * The main Activity of Debug Bottle
 */
internal class DTDrawerActivity : AppCompatActivity(), DialogsCollection.SPDialogAction, __ContentFragment.DrawerActivity {

    private var contentFragment: __ContentFragment? = null

    companion object {
        val KEY_SELECTED = "KEY_SELECTED"
        val KEY_SHOW_DRAWER = "KEY_SHOW_DRAWER"

        /**
         * Intent "selected item" must choose an string resource from items in this array.
         */
        @Suppress("unused")
        val VALUE_SELECTED = R.array.__dt_drawer_items
    }

    private val titles: Array<String> by lazy {
        resources.getStringArray(R.array.__dt_drawer_items)
    }

    private val drawerLayout by lazy { findViewById(R.id.__dt_drawer_layout) as DrawerLayout }

    private val drawerRoot by lazy { findViewById(R.id.__dt_drawer_root) as ViewGroup }

    private val drawerListView by lazy {
        (findViewById(R.id.__dt_left_drawer) as ListView).apply {
            adapter = DrawerAdapter(titles)
            setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                selectItem(position)
            }
        }
    }

    private val contentFrame by lazy { findViewById(R.id.__dt_content_frame) as ViewGroup }

    private val drawerToggle by lazy {
        object: ActionBarDrawerToggle(this, drawerLayout, R.string.__dt_drawer_open, R.string.__dt_drawer_close) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }
        }.apply { setHomeAsUpIndicator(R.drawable.__dt_ic_bottle_24dp) }
    }

    private val infoLayout by lazy {
        (findViewById(R.id.__dt_info) as ViewGroup).apply {
            setOnClickListener {
                AlertDialog.Builder(this@DTDrawerActivity)
                        .setIcon(R.drawable.__dt_ic_bottle_24dp)
                        .setTitle(R.string.__dt_info)
                        .setMessage(R.string.__dt_info_introduction)
                        .setNegativeButton(R.string.__dt_close) { dialog, witch -> }
                        .setNeutralButton(R.string.__dt_github) { dialog, witch ->
                            val url = DTSettings.GITHUB_URL
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(url)
                            startActivity(intent)
                        }
                        .show()
            }
        }
    }

    private val introLayout by lazy {
        (findViewById(R.id.__dt_helper) as ViewGroup).apply {
            setOnClickListener {

                // To start IntroductionActivity, need a AppCompatTheme.
                // But this module does't has an access to AppCompatTheme.
                val intent = Intent(this@DTDrawerActivity, IntroductionActivity::class.java)
                val clazz = ContextThemeWrapper::class.java
                val method = clazz.getMethod("getThemeResId")
                method.isAccessible = true
                val themeResId = method.invoke(this) as Int
                intent.putExtra("theme", themeResId)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityManager.isUserAMonkey()) {
            finish()
            return
        }
        setContentView(R.layout.__activity_dt_drawer)
        drawerLayout.addDrawerListener(drawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.__dt_ic_bottle_24dp)
        drawerListView; infoLayout; introLayout

        val selectedItem = intent?.extras?.getInt(KEY_SELECTED)
        val showDrawer = intent?.extras?.getBoolean(KEY_SHOW_DRAWER)

        if (null == selectedItem || 0 == selectedItem) {
            selectItem(0)
            if (showDrawer?: false) {
                drawerLayout.openDrawer(Gravity.LEFT)
            }
        } else {
            selectItemByRes(selectedItem)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isSystemAlertPermissionGranted()) {
            runBubbleService()
        }
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
        if (!(contentFragment?.onBackPressed()?: false)) {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawers()
            } else if (!(contentFragment?.isHome?: false)) {
                selectItem(0)
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun updateSPViews() {
        val f = supportFragmentManager.findFragmentByTag(__SPViewerFragment.TAG) as __SPViewerFragment?
        f?.updateSPViews()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode > -1 && requestCode < __StatusFragment.permissions.size) {
            //val permission = __StatusFragment.permissions[requestCode]
            updatePermissionStatus()
        }

        /**
         * Bubble service must run before add bubble view.
         */
        if (!isSystemAlertPermissionGranted()) {
            requestingPermissionDrawOverOtherApps(null)
        } else {
            runBubbleService()
        }
    }

    private fun updatePermissionStatus() {
        val f = supportFragmentManager.findFragmentByTag(__StatusFragment.TAG) as __StatusFragment?
        f?: return
        f.updatePermissionStatus()
    }

    private fun s(@IdRes id: Int): String = resources.getString(id)

    override fun selectItemByRes(@StringRes res: Int) {
        selectItem(getString(res))
    }

    private fun selectItem(title: String) {
        val i = titles.indexOf(title)
        if (-1 == i) return
        selectItem(i)
    }

    private fun selectItem(position: Int) {

        var fragment: __ContentFragment? = null

        // Create fragment by it's title
        when (titles[position]) {

            // Status fragment
            s(R.string.__dt_status) -> {
                fragment = __StatusFragment()
            }

            // All Activities fragment
            s(R.string.__dt_all_activities) -> {
                fragment = __InjectorFragment.newInstance(__InjectorFragment.TYPE_ALL_ACTIVITIES)
            }

            // Intent entries fragment
            s(R.string.__dt_intents) -> {
                fragment =  __InjectorFragment.newInstance(__InjectorFragment.TYPE_INTENT)
            }

            // Runnable entries fragment
            s(R.string.__dt_runnable) -> {
                fragment = __InjectorFragment.newInstance(__InjectorFragment.TYPE_RUNNABLE)
            }

            // Shared Preferences editor fragment
            s(R.string.__dt_sp_viewer) -> {
                fragment = __SPViewerFragment()
            }

            // Block canary fragment
            s(R.string.__dt_blocks) -> {
                fragment = __DisplayBlockFragment()
            }

            // Network sniffer fragment
            s(R.string.__dt_network_traffics) -> {
                fragment = __DisplayHttpBlockFragment()
            }

            // Leak canary fragment
            s(R.string.__dt_leaks) -> {
                if (RunningFeatureMgr.has(RunningFeatureMgr.LEAK_CANARY)) {
                    val intent = Intent(this, DisplayLeakActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, R.string.__dt_should_enable_leak_canary, Toast.LENGTH_SHORT).show()
                }
                return
            }

            // Crash report fragment
            s(R.string.__dt_crashes) -> {
                fragment = __DisplayCrashBlockFragment()
            }

            // Feedback fragment
            s(R.string.__dt_feedback) -> {
                fragment = __WebViewFragment.newInstance("${DTSettings.GITHUB_URL}/issues")
            }

            // Github fragment
            s(R.string.__dt_project) -> {
                fragment = __WebViewFragment.newInstance(DTSettings.GITHUB_URL)
            }

            // Settings fragment
            s(R.string.__dt_settings) -> {
                fragment = __SettingsFragment()
            }

            // Test
            s(R.string.__dt_black_box_testing) -> {
                if (isSystemAlertPermissionGranted()) {
                    fragment = __TestSettingsFragment()
                } else {
                    requestingPermissionDrawOverOtherApps(null)
                    return
                }
            }

            // None
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

        // Menu item's title to icon map
        private val menu by lazy {
            titles.map {
                // Icon color is #8A000000
                DrawerMenuItem(it, when (it) {
                    s(R.string.__dt_status) -> R.drawable.__ic_home_black_24dp
                    s(R.string.__dt_all_activities) -> R.drawable.__ic_find_in_page_black_24dp
                    s(R.string.__dt_intents) -> R.drawable.__ic_android_black_24dp
                    s(R.string.__dt_runnable) -> R.drawable.__ic_clear_all_black_24dp
                    s(R.string.__dt_sp_viewer) -> R.drawable.__ic_edit_black_24dp
                    s(R.string.__dt_blocks) -> R.drawable.__ic_block_black_24dp
                    s(R.string.__dt_network_traffics) -> R.drawable.__ic_http_black_24dp
                    s(R.string.__dt_leaks) -> R.drawable.__ic_bug_report_black_24dp
                    s(R.string.__dt_settings) -> R.drawable.__ic_settings_black_24dp
                    s(R.string.__dt_crashes) -> R.drawable.__ic_report_problem_black_24dp
                    s(R.string.__dt_feedback) -> R.drawable.__ic_feedback_black_24dp
                    s(R.string.__dt_project) -> R.drawable.__ic_code_black_24dp
                    s(R.string.__dt_black_box_testing) -> R.drawable.__ic_fast_forward_black_24dp
                    else -> R.drawable.__ic_info_outline_black_24dp
                })
            }
        }

        override fun getCount(): Int = titles.size

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var view = convertView
            val item = menu[position]
            (view?.let { view?.tag as ViewHolder }?: ViewHolder().apply {
                view = LayoutInflater.from(this@DTDrawerActivity).inflate(R.layout.__item_drawer_menu, parent, false)
                icon = view?.findViewById(R.id.__dt_icon) as ImageView
                title = view?.findViewById(R.id.__dt_item_title) as TextView
                view?.tag = this
            }).apply {
                icon?.setImageResource(item.icon)
                title?.text = item.title
            }
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