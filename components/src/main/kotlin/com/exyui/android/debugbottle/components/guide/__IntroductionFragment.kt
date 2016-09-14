package com.exyui.android.debugbottle.components.guide

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatCheckBox
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.exyui.android.debugbottle.components.R
import com.exyui.android.debugbottle.components.DTSettings

/**
 * Created by yuriel on 9/10/16.
 */
class __IntroductionFragment: Fragment() {
    companion object {
        private val BACKGROUND_COLOR = "backgroundColor"
        private val PAGE = "page"

        fun newInstance(backgroundColor: Int, page: Int): __IntroductionFragment {
            val result = __IntroductionFragment()
            val b = Bundle()
            b.putInt(BACKGROUND_COLOR, backgroundColor)
            b.putInt(PAGE, page)
            result.arguments = b
            return result
        }

        private val binder1 = object: ViewBinder() {
            override val layoutRes: Int = R.layout.__fragment_intro_1
            override val titleRes: Int = R.string.__dt_intro_title_welcome
            override val iconAppAlpha: Float = 0f
            override val iconArrowAlpha: Float = 0f
            override val iconBottleAlpha: Float = 1f
            override val textAlpha: Float = 1f
            override val done = false

            override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {

            }
        }

        private val binder2 = object: ViewBinder() {
            override val layoutRes: Int = R.layout.__fragment_intro_2
            override val titleRes: Int = R.string.__dt_intro_title_check_permission
            override val iconAppAlpha: Float = 1f
            override val iconArrowAlpha: Float = 1f
            override val iconBottleAlpha: Float = 1f
            override val textAlpha: Float = -1f
            override val done = false

            internal val permissions = listOf(
                    /*Manifest.permission.SYSTEM_ALERT_WINDOW,*/
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
            )

            override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
                checkPermission()
            }

            override fun updatePermission() {
                checkPermission()
            }

            fun checkPermission(): Boolean {
                val storage = v(R.id.__dt_write_external_storage) as ImageView
                val phoneState = v(R.id.__dt_read_phone_state) as ImageView
                val submit = v(R.id.__dt_request_permission) as TextView

                var result: Boolean = true
                if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    storage.setImageResource(R.drawable.__ic_check_circle_white_24dp)
                } else {
                    storage.setImageResource(R.drawable.__ic_security_white_24dp)
                    result = false
                }

                if (hasPermission(Manifest.permission.READ_PHONE_STATE)) {
                    phoneState.setImageResource(R.drawable.__ic_check_circle_white_24dp)
                } else {
                    phoneState.setImageResource(R.drawable.__ic_security_white_24dp)
                    result = false
                }

                if (!result) {
                    submit.setText(R.string.__dt_intro_request_permissions)
                    submit.setOnClickListener { requestPermission() }
                } else {
                    submit.setText(R.string.__dt_intro_permission_done)
                    submit.setOnClickListener {  }
                }

                return result
            }

            private fun hasPermission(permission: String) = PackageManager.PERMISSION_DENIED != ContextCompat.checkSelfPermission(context!!, permission)

            private fun requestPermission() {
                context?: return

                for (p in permissions) {
                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(context!!, p) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(context as Activity, arrayOf(p), 1)
                    }
                }
            }

        }

        private val binder3 = object: ViewBinder() {
            override val layoutRes: Int = R.layout.__fragment_intro_3
            override val titleRes: Int = R.string.__dt_intro_title_enable_features
            override val iconAppAlpha: Float = 1f
            override val iconArrowAlpha: Float = 1f
            override val iconBottleAlpha: Float = 1f
            override val textAlpha: Float = -1f
            override val done = false

            override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
                val bottle = v(R.id.__dt_enable_bottle) as AppCompatCheckBox
                val strict = v(R.id.__dt_enable_strict_mode) as AppCompatCheckBox
                val leak = v(R.id.__dt_enable_leak_canary) as AppCompatCheckBox
                val block = v(R.id.__dt_enable_block_canary) as AppCompatCheckBox
                val sniffer = v(R.id.__dt_enable_http_sniffer) as AppCompatCheckBox

                bottle.setOnCheckedChangeListener { compoundButton, b ->
                    DTSettings.setBottleEnable(b)
                }

                strict.setOnCheckedChangeListener { compoundButton, b ->
                    DTSettings.setStrictMode(b)
                }

                leak.setOnCheckedChangeListener { compoundButton, b ->
                    DTSettings.setLeakCanaryEnable(b)
                }

                block.setOnCheckedChangeListener { compoundButton, b ->
                    DTSettings.setBlockCanaryEnable(b)
                }

                sniffer.setOnCheckedChangeListener { compoundButton, b ->
                    DTSettings.setNetworkSniff(b)
                }
            }
        }

        private val binder4 = object: ViewBinder() {
            override val layoutRes: Int = R.layout.__fragment_intro_4
            override val titleRes: Int = R.string.__dt_intro_title_done
            override val iconAppAlpha: Float = 1f
            override val iconArrowAlpha: Float = 0.3f
            override val iconBottleAlpha: Float = 0.3f
            override val textAlpha: Float = -1f
            override val done = true

            override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {

            }
        }
    }

    private var backgroundColor: Int? = null
    private var page: Int? = null
    private val binder by lazy {
        when (page) {
            0 -> binder1
            1 -> binder2
            2 -> binder3
            3 -> binder4
            else -> throw NotImplementedError("Fragment must contain a \"$PAGE\" argument!")
        }
    }

    @Throws(RuntimeException::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!arguments.containsKey(BACKGROUND_COLOR))
            throw RuntimeException("Fragment must contain a \"$BACKGROUND_COLOR\" argument!")
        backgroundColor = arguments.getInt(BACKGROUND_COLOR)

        if (!arguments.containsKey(PAGE))
            throw RuntimeException("Fragment must contain a \"$PAGE\" argument!")
        page = arguments.getInt(PAGE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = binder.getView(inflater, container, savedInstanceState)
        view?.tag = FragmentPageTag(page!!, getColor(), binder.iconAppAlpha,
                binder.iconArrowAlpha, binder.iconBottleAlpha, binder.textAlpha, binder.done)
        return view
    }

    override fun onResume() {
        super.onResume()
        updatePermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binder.release()
    }

    fun getColor() = backgroundColor!!

    fun updatePermission() {
        binder.updatePermission()
    }

    private abstract class ViewBinder {
        var rootView: View? = null
        val context: Context?
            get() = rootView?.context
        abstract val layoutRes: Int
        abstract val titleRes: Int
        abstract val iconAppAlpha: Float
        abstract val iconArrowAlpha: Float
        abstract val iconBottleAlpha: Float
        abstract val textAlpha: Float
        abstract val done: Boolean

        abstract fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)

        open fun release() {
            rootView = null
        }

        fun v(@IdRes id: Int): View? = rootView?.findViewById(id)

        fun getView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            rootView = inflater.inflate(layoutRes, container, false)
            (v(R.id.__dt_title) as TextView).setText(titleRes)
            createView(inflater, container, savedInstanceState)
            return rootView
        }

        open fun updatePermission() {}
    }
}