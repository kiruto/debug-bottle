package com.exyui.android.debugbottle.components.socks

import android.os.Message
import com.exyui.android.debugbottle.components.DTSettings
import java.net.ServerSocket
import android.os.Bundle
import android.os.Handler
import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream


/**
 * Created by yuriel on 11/15/16.
 */
internal class DTSocketConnector {
    private val TAG = "DTSocketConnector"
    private val MSG = "MSG"
    private val SOCKET_MSG = 0x45
    private val serverSocket by lazy { ServerSocket(DTSettings.socketPort) }
    private var onAcceptListener: (Message) -> Unit = { defaultListener(it) }
    private val handler by lazy {
        object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == SOCKET_MSG) onAcceptListener.invoke(msg)
            }
        }
    }

    private var isLoop = false

    fun setOnAcceptListener(l: (Message) -> Unit) {
        onAcceptListener = l
    }

    fun accept() {
        isLoop = true
        try {
            while (isLoop) {
                val socket = serverSocket.accept()
                Log.d(TAG, "accept")
                val inputStream = DataInputStream(socket.inputStream)
                val outputStream = DataOutputStream(socket.outputStream)
                val msg = inputStream.readUTF()
                val message = Message.obtain()
                message.what = SOCKET_MSG
                val bundle = Bundle()
                bundle.putString(MSG, msg)
                message.data = bundle
                handler.sendMessage(message)
                socket.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            unaccept()
        } finally {
            try {
                serverSocket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun unaccept() {
        isLoop = false
    }

    private fun defaultListener(message: Message) {
        Log.d(TAG, message.data.getString(MSG))
    }
}