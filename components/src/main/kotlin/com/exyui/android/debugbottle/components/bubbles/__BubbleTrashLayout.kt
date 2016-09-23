package com.exyui.android.debugbottle.components.bubbles

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.os.Vibrator
import android.util.AttributeSet
import com.exyui.android.debugbottle.components.R

/**
 * Created by yuriel on 9/22/16.
 */
internal class __BubbleTrashLayout : __BubbleBaseLayout {
    constructor(context: Context): super(context)
    constructor(context: Context, attr: AttributeSet): super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int): super(context, attr, defStyleAttr)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attr, defStyleAttr, defStyleRes)

    val VIBRATION_DURATION_IN_MS = 70L
    private var magnetismApplied = false
    private var attachedToWindow = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attachedToWindow = false
    }

    override fun setVisibility(visibility: Int) {
        if (attachedToWindow) {
            if (visibility != getVisibility()) {
                if (visibility == VISIBLE) {
                    playAnimation(R.animator.__bubble_trash_shown_animator)


                } else {
                    playAnimation(R.animator.__bubble_trash_hide_animator)
                }
            }
        }
        super.setVisibility(visibility)
    }

    fun applyMagnetism() {
        if (!magnetismApplied) {
            magnetismApplied = true
            playAnimation(R.animator.__bubble_trash_shown_magnetism_animator)
        }
    }

    fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VIBRATION_DURATION_IN_MS)
    }

    fun releaseMagnetism() {
        if (magnetismApplied) {
            magnetismApplied = false
            playAnimation(R.animator.__bubble_trash_hide_magnetism_animator)
        }
    }

    private fun playAnimation(animationResourceId: Int) {
        if (!isInEditMode) {
            val animator = AnimatorInflater.loadAnimator(context, animationResourceId) as AnimatorSet
            animator.setTarget(getChildAt(0))
            animator.start()
        }
    }

}