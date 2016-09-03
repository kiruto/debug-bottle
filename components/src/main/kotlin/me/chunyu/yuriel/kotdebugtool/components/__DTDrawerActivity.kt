package me.chunyu.yuriel.kotdebugtool.components

import android.app.Activity
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import me.chunyu.yuriel.kotdebugtool.components.fragments.__ContentFragment
import me.chunyu.yuriel.kotdebugtool.components.fragments.__DisplayHttpBlockFragment
import me.chunyu.yuriel.kotdebugtool.components.fragments.__SettingsFragment

/**
 * Created by yuriel on 9/3/16.
 */
internal class __DTDrawerActivity: AppCompatActivity() {

    private var contentFragment: __ContentFragment? = null

    private val titles by lazy {
        resources.getStringArray(R.array.__dt_drawer_items)
    }

    private val drawerLayout by lazy {
        val result = findViewById(R.id.__dt_drawer_layout) as DrawerLayout
        result
    }

    private val drawerListView by lazy {
        val result = findViewById(R.id.__dt_left_drawer) as ListView
        result.adapter = ArrayAdapter<String>(this, R.layout.__dt_item_drawer_list, titles)
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
        object: ActionBarDrawerToggle(this, drawerLayout, R.string.__dt_drawer_open, R.string.__dt_drawer_close) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.__activity_dt_drawer)
        drawerLayout.addDrawerListener(drawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        drawerListView
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

    private fun selectItem(position: Int) {

        var fragment: __ContentFragment? = null

        fun s(@IdRes id: Int): String = resources.getString(id)

        when (titles[position]) {
            s(R.string.__dt_network_traffics) -> {
                fragment = __DisplayHttpBlockFragment()
            }

            s(R.string.__dt_settings) -> {
                fragment = __SettingsFragment()
            }

            else -> {

            }
        }

        if (null != fragment) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.__dt_content_frame, fragment)
                    .commit()
            contentFragment = fragment
        }

        drawerListView.setItemChecked(position, true)
        title = titles[position]
        drawerLayout.closeDrawer(drawerListView)
    }
}