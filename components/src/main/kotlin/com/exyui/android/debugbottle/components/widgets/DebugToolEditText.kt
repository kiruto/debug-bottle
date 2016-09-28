package com.exyui.android.debugbottle.components.widgets

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.widget.EditText
import java.util.*

/**
 * Created by yuriel on 8/18/16.
 */
internal class DebugToolEditText : EditText {

    private var backListener: (() -> Unit)? = null
    private var spaceListener: (() -> Unit)? = null
    private var enterListener: (() -> Unit)? = null

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context) : super(context) {
    }

//    fun setRandomBackgroundColor() {
//        setBackgroundColor(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)))
//    }

    internal fun onBackPressed(l: () -> Unit) {
        backListener = l
    }

    internal fun onSpacePressed(l: () -> Unit) {
        spaceListener = l
    }

    internal fun onEnterPressed(l: () -> Unit) {
        enterListener = l
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        return DebugToolEditTextConnection(super.onCreateInputConnection(outAttrs), true)
    }

    private inner class DebugToolEditTextConnection(target: InputConnection, mutable: Boolean) : InputConnectionWrapper(target, mutable) {

        override fun sendKeyEvent(event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL) {
                backListener?.invoke()
            } else if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_SPACE) {
                spaceListener?.invoke()
            } else if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                enterListener?.invoke()
                return false
            }
            return super.sendKeyEvent(event)
        }

//        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
//            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
//            if (beforeLength == 1 && afterLength == 0) {
//                // backspace
//                return sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) && sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
//            }
//
//            return super.deleteSurroundingText(beforeLength, afterLength)
//        }

    }
}