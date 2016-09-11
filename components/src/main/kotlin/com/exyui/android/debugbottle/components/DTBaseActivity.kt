package com.exyui.android.debugbottle.components

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

/**
 * Created by yuriel on 8/23/16.
 */
internal abstract class DTBaseActivity : AppCompatActivity() {

    protected abstract val actionbarTitle: String

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initActionBar(supportActionBar)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initActionBar(actionBar: ActionBar?) {
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(true)
        actionBar?.title = actionbarTitle
    }
}