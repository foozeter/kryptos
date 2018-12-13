package com.hayashibambi.kryptos

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.View

class UpDownArrow(context: Context, attrs: AttributeSet): View(context, attrs) {

    private val arrow = UpDownArrowDrawable(context)
    private val fitToViewSize: Boolean

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.UpDownArrow, 0, 0
        )

        arrow.arrowWidth = a.getDimensionPixelSize(
            R.styleable.UpDownArrow_upa_arrowWidth,
            arrow.arrowWidth
        )

        arrow.arrowHeight = a.getDimensionPixelSize(
            R.styleable.UpDownArrow_upa_arrowHeight,
            arrow.arrowHeight
        )

        arrow.arrowThickness = a.getDimensionPixelSize(
            R.styleable.UpDownArrow_upa_arrowThickness,
            arrow.arrowThickness.toInt()).toFloat()

        arrow.arrowColor = a.getColor(
            R.styleable.UpDownArrow_upa_arrowColor,
            arrow.arrowColor)

        arrow.breakStart = a.getFloat(
            R.styleable.UpDownArrow_upa_breakStart,
            arrow.breakStart.toFloat()).toDouble()

        arrow.breakEnd = a.getFloat(
            R.styleable.UpDownArrow_upa_breakEnd,
            arrow.breakEnd.toFloat()).toDouble()

        arrow.progress = a.getFloat(
            R.styleable.UpDownArrow_upa_progress,
            arrow.progress)

        arrow.isUpToDown = a.getBoolean(
            R.styleable.UpDownArrow_upa_isUpToDown,
            arrow.isUpToDown)

        arrow.skipBreakTime = a.getBoolean(
            R.styleable.UpDownArrow_upa_skipBreakTime,
            arrow.skipBreakTime)

        fitToViewSize = a.getBoolean(
            R.styleable.UpDownArrow_upa_fitToViewSize,
            false)

        a.recycle()

        // Call setBackground(Drawable) to set
        // the arrow drawable as a background.
        background = super.getBackground()
    }

    override fun onLayout(changed: Boolean, left: Int,
                          top: Int, right: Int, bottom: Int) {

        super.onLayout(changed, left, top, right, bottom)
        if (fitToViewSize) arrow.apply {
            arrowWidth = this@UpDownArrow.width
            arrowHeight = this@UpDownArrow.height
        }
    }

    fun setProgress(progress: Float) {
        arrow.progress = progress
    }

    override fun setBackground(background: Drawable?) {
        when {
            // Maybe, the super class will call this method in its constructor,
            // in that case, 'arrow' is not initialized yet.
            arrow == null -> super.setBackground(background)
            background == null -> super.setBackground(arrow)
            else -> super.setBackground(LayerDrawable(arrayOf(background, arrow)))
        }
    }

    override fun getBackground(): Drawable? {
        val bg = super.getBackground()
        return if (bg is LayerDrawable) bg.getDrawable(0)
        else bg
    }
}