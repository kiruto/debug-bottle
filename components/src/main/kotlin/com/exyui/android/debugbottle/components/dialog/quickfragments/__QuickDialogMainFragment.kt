package com.exyui.android.debugbottle.components.dialog.quickfragments

import android.os.Bundle
import android.support.annotation.IdRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exyui.android.debugbottle.components.R

/**
 * Created by yuriel on 10/13/16.
 */
class __QuickDialogMainFragment: __BaseQuickDialogFragment() {

    companion object {
        private val CONTAINER_ID = "CONTAINER_ID"
        val TAG = "__QuickDialogMainFragment"
        fun newInstance(@IdRes containerId: Int): __QuickDialogMainFragment {
            val result = __QuickDialogMainFragment()
            val args = Bundle()
            args.putInt(CONTAINER_ID, containerId)
            result.arguments = args
            return result
        }
    }

    val TAG = __QuickDialogMainFragment.TAG

    private var rootView: View? = null
    @IdRes private var containerId: Int? = null
    private val togglesEntry by lazy {
        val result = rootView?.findViewById(R.id.__dt_toggles)
        result?.setOnClickListener {
            val togglesFragment = __QuickDialogTogglesFragment()
            fm().beginTransaction().replace(containerId!!, togglesFragment, togglesFragment.TAG)
                    .addToBackStack(null)
                    .commit()
        }
        result
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        containerId = arguments.getInt(CONTAINER_ID)
        if (null == container)
            return null
        rootView = inflater.inflate(R.layout.__fragment_quick_main, container, false)
        bindViews()
        return rootView
    }

    private fun bindViews() {
        togglesEntry
    }
}