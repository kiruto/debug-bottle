package com.exyui.android.debugbottle.components.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.widgets.DTListItemSwitch

/**
 * Created by yuriel on 9/20/16.
 */
class __TestSettingsFragment: __ContentFragment() {
    override val TAG = __TestSettingsFragment.TAG
    private lateinit var testCtrlView: DTListItemSwitch

    companion object {
        val TAG = "__TestSettingsFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.__fragment_test_settings, container, false)
        testCtrlView = rootView.findViewById(R.id.__dt_testing_enable) as DTListItemSwitch
        testCtrlView.setOnCheckedChangeListener { view, isChecked ->
            Toast.makeText(context, "$view, $isChecked", Toast.LENGTH_SHORT).show()
        }
        return rootView
    }
}