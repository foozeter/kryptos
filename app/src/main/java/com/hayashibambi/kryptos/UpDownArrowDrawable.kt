package com.hayashibambi.kryptos

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.FloatRange
import androidx.core.math.MathUtils

internal class UpDownArrowDrawable(context: Context): Drawable() {

    companion object {
        private const val DEFAULT_ARROW_WIDTH = 48 // dip
        private const val DEFAULT_ARROW_HEIGHT = 28 // dip
        private const val DEFAULT_ARROW_THICKNESS = 8 // dip
        private const val DEFAULT_ARROW_COLOR = Color.BLACK
        private const val DEFAULT_FRACTION_BREAK_START = 0.45
        private const val DEFAULT_FRACTION_BREAK_END = 0.65
        private const val DEFAULT_PROGRESS = 0f
        private const val DEFAULT_IS_UP_TO_DOWN = true
        private const val DEFAULT_SKIP_BREAK_TIME = false
        private const val DEFAULT_RICH_DRAWING = true
        private const val DEFAULT_ARROW_CURVATURE = 0.7f // use in rich drawing
    }

    private var mode: Mode = Flip()
        set(value) {
            if (field != value) {
                field = value
                invalidateSelf()
            }
        }

    private val fraction = Fraction()
    private val paint = Paint()
    private val drawArea = Rect()

    /**
     * If this is TRUE, the arrow shape will be drawn
     * using the bezier curve (ONLY available in Flip-Mode for now).
     */
    var richDrawing = DEFAULT_RICH_DRAWING
        set(value) {
            if (field != value) {
                field = value
                invalidateSelf()
            }
        }

    @FloatRange(from = 0.0, to = 1.0)
    var arrowCurvature = DEFAULT_ARROW_CURVATURE
        set(value) {
            val v = MathUtils.clamp(value, 0f, 1f)
            if (field != v) {
                field = v
                invalidateSelf()
            }
        }

    var progress = DEFAULT_PROGRESS
        set(value) {
            val clamped = MathUtils.clamp(value, 0f, 1f)
            if (field != clamped) {
                field = clamped
                invalidateSelf()
            }
        }

    var arrowWidth = dpToPx(DEFAULT_ARROW_WIDTH, context)
        set(value) {
            if (field != value) {
                field = value
                invalidateDrawArea(bounds)
                invalidateSelf()
            }
        }

    var arrowHeight = dpToPx(DEFAULT_ARROW_HEIGHT, context)
        set(value) {
            if (field != value) {
                field = value
                invalidateDrawArea(bounds)
                invalidateSelf()
            }
        }

    var isUpToDown = DEFAULT_IS_UP_TO_DOWN
        set(value) {
            if (field != value) {
                field = value
                invalidateSelf()
            }
        }

    var arrowThickness
        get() = paint.strokeWidth
        set(value) {
            if (paint.strokeWidth != value) {
                paint.strokeWidth = value
                invalidateDrawArea(bounds)
                invalidateSelf()
            }
        }

    var arrowColor
        get() = paint.color
        set(value) {
            if (paint.color != value) {
                paint.color = value
                invalidateSelf()
            }
        }

    var breakStart
        get() = fraction.breakStart
        set(value) {
            if (fraction.breakStart != value) {
                fraction.breakStart = value
                invalidateSelf()
            }
        }

    var breakEnd
        get() = fraction.breakEnd
        set(value) {
            if (fraction.breakEnd != value) {
                fraction.breakEnd = value
                invalidateSelf()
            }
        }

    var skipBreakTime
        get() = fraction.skipBreakTime
        set(value) {
            if (fraction.skipBreakTime != value) {
                fraction.skipBreakTime = value
                invalidateSelf()
            }
        }

    init {
        breakStart = DEFAULT_FRACTION_BREAK_START
        breakEnd = DEFAULT_FRACTION_BREAK_END
        arrowColor = DEFAULT_ARROW_COLOR
        arrowThickness = dpToPx(DEFAULT_ARROW_THICKNESS, context).toFloat()
        skipBreakTime = DEFAULT_SKIP_BREAK_TIME
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.isAntiAlias = true
    }

    override fun draw(canvas: Canvas) =
        mode.draw(canvas, paint, fraction.get(progress))

    override fun getDirtyBounds() = drawArea

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        invalidateDrawArea(bounds)
    }

    fun setAsFlipMode() {
        if (mode !is Flip) {
            mode = Flip()
        }
    }

    fun setAsCrossMode() {
        if (mode !is Cross) {
            mode = Cross()
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getAlpha() = paint.alpha

    override fun getOpacity() = when {
        alpha <= 0f -> PixelFormat.TRANSPARENT
        0f < alpha && alpha < 1f -> PixelFormat.TRANSLUCENT
        else -> PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter) {
        paint.colorFilter = colorFilter
    }

    override fun getColorFilter() = paint.colorFilter

    private fun invalidateDrawArea(bounds: Rect) {
        drawArea.set(bounds)
        val g = arrowThickness / 2f
        val verticalPadding = (drawArea.height() - arrowHeight) / 2f
        val horizontalPadding = (drawArea.width() - arrowWidth) / 2f
        drawArea.inset((horizontalPadding + g).toInt(), (verticalPadding + g).toInt())
    }

    private fun Path.moveTo(x: Int, y: Int) = this.moveTo(x.toFloat(), y.toFloat())
    private fun Path.lineTo(x: Int, y: Int) = this.lineTo(x.toFloat(), y.toFloat())
    private fun Path.moveTo(point: PointF) = this.moveTo(point.x, point.y)
    private fun Path.lineTo(point: PointF) = this.lineTo(point.x, point.y)
    private fun Path.quadTo(control: PointF, end: PointF) = this.quadTo(control.x, control.y, end.x, end.y)
    private fun Canvas.drawLine(start: PointF, end: PointF, paint: Paint) = this.drawLine(start.x, start.y, end.x, end.y, paint)

    private fun dpToPx(dp: Int, context: Context)
            = (context.resources.displayMetrics.density * dp).toInt()

    private class Fraction {

        companion object {
            private const val MIN_BREAK_POSITION = 0.1
            private const val MAX_BREAK_POSITION = 0.9
        }

        @FloatRange(
            from = MIN_BREAK_POSITION,
            to = MAX_BREAK_POSITION)
        var breakStart = DEFAULT_FRACTION_BREAK_START
            set(value) {
                field = Math.min(
                    MathUtils.clamp(value, MIN_BREAK_POSITION, MAX_BREAK_POSITION),
                    breakEnd)
            }

        @FloatRange(
            from = MIN_BREAK_POSITION
            , to = MAX_BREAK_POSITION)
        var breakEnd = DEFAULT_FRACTION_BREAK_END
            set(value) {
                field = Math.max(
                    breakStart,
                    MathUtils.clamp(value, MIN_BREAK_POSITION, MAX_BREAK_POSITION))
            }

        var skipBreakTime = DEFAULT_SKIP_BREAK_TIME

        /**
         * t: 0.0        -> breakStart / return: 1.0 -> 0.0
         * t: breakStart -> breakEnd   / return: 0.0 -> 0.0
         * t: breakEnd   -> 1.0        / return: 0.0 -> -1.0
         */
        @FloatRange(from = -1.0, to = 1.0)
        fun get(@FloatRange(from = 0.0, to = 1.0) t: Float): Float =
            if (skipBreakTime) 1f - 2f * t
            else when {
                t < breakStart -> (1f - t / breakStart).toFloat()
                t < breakEnd -> 0f
                else -> -((t - breakEnd) / (1 - breakEnd)).toFloat()
            }
    }

    private interface Mode {
        fun draw(canvas: Canvas, paint: Paint, fraction: Float)
    }

    private inner class Flip: Mode {

        private val path = Path()
        private val left = PointF()
        private val right = PointF()
        private val center = PointF()
        private val bezierStart = PointF()
        private val bezierEnd = PointF()

        override fun draw(canvas: Canvas, paint: Paint, fraction: Float) {
            calculatePosition(fraction)
            path.reset()
            path.moveTo(left)

            if (richDrawing) {
                path.lineTo(bezierStart)
                path.quadTo(center, bezierEnd)
            } else {
                path.lineTo(center)
            }

            path.lineTo(right)
            canvas.drawPath(path, paint)
        }

        private fun calculatePosition(fraction: Float) {
            var dy = drawArea.height() / 2 * fraction
            if (!isUpToDown) dy *= -1
            val centerY = drawArea.centerY()
            left.x = drawArea.left.toFloat()
            left.y = centerY + dy
            center.x = drawArea.centerX().toFloat()
            center.y = centerY - dy
            right.x = drawArea.right.toFloat()
            right.y = left.y

            if (richDrawing) {
                bezierStart.x = left.x + (center.x - left.x) * arrowCurvature
                bezierStart.y = left.y + (center.y - left.y) * arrowCurvature
                bezierEnd.x = right.x + (center.x - right.x) * arrowCurvature
                bezierEnd.y = right.y + (center.y - right.y) * arrowCurvature
            }
        }
    }

    /**
     * This class does not support rich drawing.
     */
    private inner class Cross: Mode {

        private val path = Path()
        private val leftBelow = PointF()
        private val leftAbove = PointF()
        private val rightBelow = PointF()
        private val rightAbove = PointF()

        override fun draw(canvas: Canvas, paint: Paint, fraction: Float) {
            when {

                (fraction == 1f && isUpToDown) || (fraction == -1f && !isUpToDown) -> {
                    path.reset()
                    path.moveTo(drawArea.left, drawArea.bottom)
                    path.lineTo(drawArea.centerX(), drawArea.top)
                    path.lineTo(drawArea.right, drawArea.bottom)
                    canvas.drawPath(path, paint)
                }

                (fraction == 1f && !isUpToDown) || (fraction == -1f && isUpToDown) -> {
                    path.reset()
                    path.moveTo(drawArea.left, drawArea.top)
                    path.lineTo(drawArea.centerX(), drawArea.bottom)
                    path.lineTo(drawArea.right, drawArea.top)
                    canvas.drawPath(path, paint)
                }

                else -> {
                    calculatePosition(fraction)
                    canvas.drawLine(leftBelow, leftAbove, paint)
                    canvas.drawLine(rightBelow, rightAbove, paint)
                }
            }
        }

        private fun calculatePosition(fraction: Float) {
            val q = drawArea.width() / 4f
            val bl = drawArea.left + q
            val br = drawArea.right - q
            var dq = q * fraction
            if (!isUpToDown) dq *= -1
            leftBelow.x = bl - dq
            leftBelow.y = drawArea.bottom.toFloat()
            leftAbove.x = br - dq
            leftAbove.y = drawArea.top.toFloat()
            rightBelow.x = br + dq
            rightBelow.y = leftBelow.y
            rightAbove.x = bl + dq
            rightAbove.y = leftAbove.y
        }
    }
}