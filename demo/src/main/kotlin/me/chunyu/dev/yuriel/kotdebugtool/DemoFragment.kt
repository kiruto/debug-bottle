package me.chunyu.dev.yuriel.kotdebugtool

import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import java.io.FileInputStream
import java.io.IOException

/**
 * Created by yuriel on 8/9/16.
 */
class DemoFragment : Fragment(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.__fragment_main, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button1 = view.findViewById(R.id.button1) as Button
        val button2 = view.findViewById(R.id.button2) as Button
        val button3 = view.findViewById(R.id.button3) as Button
        val button4 = view.findViewById(R.id.button4) as Button
        val button5 = view.findViewById(R.id.button5) as Button

        button1.setOnClickListener(this)
        button2.setOnClickListener(this)
        button3.setOnClickListener(this)
        button4.setOnClickListener(this)
        button5.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button1 ->
                try {
                    Thread.sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    Log.e(DEMO_FRAGMENT, "onClick of R.id.button1: ", e)
                }

            R.id.button2 -> for (i in 0..99) {
                readFile()
            }
            R.id.button3 -> {
                val result = compute()
                println(result)
            }
            R.id.button4 -> {
                startAsyncTask()
                activity.finish()
            }
            R.id.button5 -> {
                sendRequest()
            }
            else -> {
            }
        }
    }

    fun startAsyncTask() {
        // This async task is an anonymous class and therefore has a hidden reference to the outer
        // class MainActivity. If the activity gets destroyed before the task finishes (e.g. rotation),
        // the activity instance will leak.
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                // Do some slow work in background
                SystemClock.sleep(20000)
                return null
            }
        }.execute()
    }

    fun sendRequest() {

        object: AsyncTask<Void, Void, Any?>() {
            override fun doInBackground(vararg params: Void): Any? {
                val request = Request.Builder()
                        .url("http://dev.exyui.com/")
                        .get()
                        .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "1fe7c430-c1d8-d59c-2145-5ac4df753f37")
                        .build()

                return DemoApplication.httpClient.newCall(request).execute()
            }
        }.execute()
    }

    companion object {

        private val DEMO_FRAGMENT = "DemoFragment"

        fun newInstance(): DemoFragment {
            return DemoFragment()
        }

        private fun compute(): Double {
            var result = 0.0
            for (i in 0..999999) {
                result += Math.acos(Math.cos(i.toDouble()))
                result -= Math.asin(Math.sin(i.toDouble()))
            }
            return result
        }

        private fun readFile() {
            var reader: FileInputStream? = null
            try {
                reader = FileInputStream("/proc/stat")
                while (reader.read() != -1);
            } catch (e: IOException) {
                Log.e(DEMO_FRAGMENT, "readFile: /proc/stat", e)
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        Log.e(DEMO_FRAGMENT, " on close reader ", e)
                    }

                }
            }
        }
    }
}