package com.exyui.android.debugbottle.components.widgets

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.*
import com.exyui.android.debugbottle.components.R

/**
 * Created by yuriel on 9/26/16.
 */
abstract class __FloatAnimatedDialog : DialogFragment() {
    abstract val title: Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val result = Dialog(activity)
        result.window.requestFeature(Window.FEATURE_NO_TITLE)
        result.setContentView(createView())
        return result
    }

    override fun onStart() {
        super.onStart()

        dialog.window.attributes.gravity = Gravity.TOP
        dialog.window.attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window.attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window.attributes.windowAnimations = R.style.__DialogAnimation
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)

        /** THIS WILL ADD AN ANIMATION WITH DIALOG APPEARANCE
        //Grab the window of the dialog, and change the width
        val lp = WindowManager.LayoutParams()
        val window = dialog.window
        lp.copyFrom(window.attributes)

        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp

        val attr = dialog.window.attributes
        attr.gravity = Gravity.TOP
        attr.flags = attr.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()

        window.attributes.gravity = Gravity.TOP

        val decorView = dialog.window.decorView
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f))
        scaleDown.duration = animationDuration
        scaleDown.start()
        */
    }

    override fun dismiss() {
        /** THIS WILL ADD AN ANIMATION WITH DIALOG DISMISS
        val decorView = dialog.window.decorView

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.0f),
                PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f))
        scaleDown.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                super@__FloatAnimatedDialog.dismiss()
            }

            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        scaleDown.duration = animationDuration
        scaleDown.start()
        */
        super.dismiss()
    }

    abstract fun createView(): View
}