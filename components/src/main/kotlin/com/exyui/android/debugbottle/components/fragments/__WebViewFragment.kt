package com.exyui.android.debugbottle.components.fragments

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import com.exyui.android.debugbottle.components.R

/**
 * Created by yuriel on 9/12/16.
 */
class __WebViewFragment: __ContentFragment(), View.OnClickListener {
    override val TAG: String = __WebViewFragment.TAG
    private var webView: WebView? = null
    private var addressView: TextView? = null
    private var backView: View? = null
    private var refreshView: View? = null
    private var url: String? = null
    private var urlBar: ViewGroup? = null

    companion object {
        private val TAG = "__WebViewFragment"
        private val URL = "url"

        fun newInstance(url: String): __WebViewFragment {
            return __WebViewFragment().apply {
                val args = Bundle()
                args.putString(URL, url)
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = arguments.getString(URL)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.__fragment_web_view, container, false).apply {
            webView = findViewById(R.id.__dt_web_view) as WebView
            addressView = findViewById(R.id.__dt_address) as TextView
            backView = findViewById(R.id.__dt_back)
            refreshView = findViewById(R.id.__dt_refresh)
            urlBar = findViewById(R.id.__dt_url_bar) as ViewGroup

            addressView?.setOnClickListener(this@__WebViewFragment)
            backView?.setOnClickListener(this@__WebViewFragment)
            refreshView?.setOnClickListener(this@__WebViewFragment)

            webView?.setWebViewClient(DTWebViewClient())
            webView?.settings?.javaScriptEnabled = true
            webView?.load(url)

            setHasOptionsMenu(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        menu.add(R.string.__dt_share)
                .setIcon(R.drawable.__ic_share_black_24dp)
                .setOnMenuItemClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, url)
                    startActivity(Intent.createChooser(intent, getString(R.string.__block_canary_share_with)))
                    true
                }
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

    override fun onBackPressed(): Boolean {
        return if (webView?.canGoBack() == true) {
            webView?.goBack()
            true
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.__dt_address -> {
                AlertDialog.Builder(activity)
                        .setIcon(R.drawable.__ic_github_black_24dp)
                        .setTitle(R.string.__dt_full_url)
                        .setMessage(url)
                        .setPositiveButton(R.string.__dt_copy) { _, _ ->
                            copyUrlToClipboard()
                        }
                        .setNeutralButton(R.string.__dt_open_in_browser) { _, _ ->
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(url)
                            startActivity(intent)
                        }
                        .show()
            }
            R.id.__dt_back -> {
                if (webView?.canGoBack() == true) {
                    webView?.goBack()
                }
            }
            R.id.__dt_refresh -> {
                webView?.reload()
            }
        }
    }

    private fun copyUrlToClipboard() {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(URL, url)
        clipboard.primaryClip = clip
        Toast.makeText(activity, R.string.__dt_copied, Toast.LENGTH_SHORT).show()
    }

    private fun WebView.load(url: String?) {
        url?: return
        this@__WebViewFragment.url = url
        addressView?.text = url
        loadUrl(url)
    }

    private inner class DTWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (!url.startsWith("http://github.com/") || !url.startsWith("https://github.com/")) {
                view.settings.javaScriptEnabled = false
            }
            view.load(url)
            return true
        }
    }

    private fun ViewGroup.fadeIn() {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = 1000

        val animation = AnimationSet(false)
        animation.addAnimation(fadeIn)

    }

    private fun ViewGroup.fadeOut() {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.startOffset = 1000
        fadeOut.duration = 1000

        val animation = AnimationSet(false)
        animation.addAnimation(fadeOut)
    }
}