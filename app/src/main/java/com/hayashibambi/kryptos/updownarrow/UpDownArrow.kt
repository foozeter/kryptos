package com.hayashibambi.kryptos.updownarrow

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.hayashibambi.kryptos.R
import com.hayashibambi.kryptos.linkagebottomsheetlayout.linkagebehavior.LinkagePropertyBehavior

class UpDownArrow(context: Context, attrs: AttributeSet)
    : View(context, attrs), LinkagePropertyBehavior.ValueConsumer {

    companion object {
        private const val ATTR_MODE_FLIP = 0
        private const val ATTR_MODE_CROSS = 1
    }

    var progress: Float
        set(value) { arrow.progress = value }
        get() = arrow.progress

    var isUpToDown: Boolean
        set(value) { arrow.isUpToDown = value }
        get() = arrow.isUpToDown

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

        arrow.arrowCurvature = a.getFloat(
            R.styleable.UpDownArrow_upa_arrowCurvature,
            arrow.arrowCurvature)

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

        arrow.richDrawing = a.getBoolean(
            R.styleable.UpDownArrow_upa_richDrawing,
            arrow.richDrawing)

        fitToViewSize = a.getBoolean(
            R.styleable.UpDownArrow_upa_fitToViewSize,
            false)

        val mode = a.getInt(
            R.styleable.UpDownArrow_upa_mode,
            ATTR_MODE_FLIP
        )

        a.recycle()

        when (mode) {
            ATTR_MODE_FLIP -> arrow.setAsFlipMode()
            ATTR_MODE_CROSS -> arrow.setAsCrossMode()
            else -> throw IllegalStateException(
                "unknown mode: $mode")
        }

        arrow.callback = this
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        arrow.draw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        arrow.setBounds(0, 0, w, h)
    }

    override fun onLayout(changed: Boolean, left: Int,
                          top: Int, right: Int, bottom: Int) {

        super.onLayout(changed, left, top, right, bottom)
        if (fitToViewSize) arrow.apply {
            arrowWidth = this@UpDownArrow.width
            arrowHeight = this@UpDownArrow.height
        }
    }

    // This is VERY IMPORTANT!!
    // See invalidateDrawable(Drawable) method in View class.
    override fun verifyDrawable(who: Drawable) =
        who == arrow || super.verifyDrawable(who)

    override fun supplyInterpolatedValue(value: Float) {
        progress = value
    }
}