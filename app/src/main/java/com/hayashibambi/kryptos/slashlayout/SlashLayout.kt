package com.hayashibambi.kryptos.slashlayout

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.core.math.MathUtils
import com.hayashibambi.kryptos.R

open class SlashLayout(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int)
    : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val DEFAULT_SHADOW_DEPTH = 0.15f
        private const val DEFAULT_SHADOW_SIZE = 8 // dip
        private const val DEFAULT_HEIGHT_DIFF = 64 // dip
        private const val DEFAULT_CORNER_RADIUS = 16 // dip
        private const val DEFAULT_BACKGROUND_COLOR = Color.WHITE
        private const val DEFAULT_EAT_TOUCH_EVENT = false
        private const val DEFAULT_AUTO_TOP_PADDING = false
    }

    private val autoTopPadding: Boolean

    @Px
    private val heightDiff: Int

    @Px
    private val cornerRadius: Int

    @Px
    private val shadowSize: Int

    @FloatRange(from = 0.0, to = 1.0)
    private val shadowDepth: Float

    @ColorInt
    private val backgroundColor: Int

    @ColorInt
    private val shadowEndColor = Color.TRANSPARENT

    private val shadowStartColor; get() = Color.argb((shadowDepth*255).toInt(), 0, 0, 0)

    /**
     * This is useful when use this view as a bottom sheet.
     */
    private val eatTouchEvent: Boolean

    private val shape = Path()

    private val leftTopCornerShadow = Path()

    private val rightTopCornerShadow = Path()

    private val topEdgeShadow = Path()

    private val shapePaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val shadowPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var leftTopCornerShadowShader: RadialGradient? = null

    private var rightTopCornerShadowShader: RadialGradient? = null

    private var topEdgeShadowShader: LinearGradient? = null

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int): this(context, attrs, defStyleAttr, 0)

    constructor(
        context: Context,
        attrs: AttributeSet?): this(context, attrs, 0, 0)

    constructor(context: Context): this(context, null, 0, 0)

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SlashLayout, 0, 0)

        heightDiff = a.getDimensionPixelSize(
            R.styleable.SlashLayout_slash_heightDiff,
            dpToPx(DEFAULT_HEIGHT_DIFF))

        cornerRadius = a.getDimensionPixelSize(
            R.styleable.SlashLayout_slash_cornerRadius,
            dpToPx(DEFAULT_CORNER_RADIUS))

        shadowSize = a.getDimensionPixelSize(
            R.styleable.SlashLayout_slash_shadowSize,
            dpToPx(DEFAULT_SHADOW_SIZE))

        shadowDepth = MathUtils.clamp(
            a.getFloat(
                R.styleable.SlashLayout_slash_shadowDepth,
                DEFAULT_SHADOW_DEPTH
            ), 0f, 1f)

        backgroundColor = a.getColor(
            R.styleable.SlashLayout_slash_backgroundColor,
            DEFAULT_BACKGROUND_COLOR
        )

        eatTouchEvent = a.getBoolean(
            R.styleable.SlashLayout_slash_eatTouchEvent,
            DEFAULT_EAT_TOUCH_EVENT
        )

        autoTopPadding = a.getBoolean(
            R.styleable.SlashLayout_slash_autoTopPadding,
            DEFAULT_AUTO_TOP_PADDING
        )

        a.recycle()
        initialize()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if(changed && autoTopPadding) insertAutoTopPadding()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val parent = parent
        if (parent is SpecialCoordinatorLayout && this.id != 0) {
            parent.addTouchDetectionDelegate(
                TouchDetectionDelegate(
                    this.id
                )
            )
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        val parent = parent
        if (parent is SpecialCoordinatorLayout && this.id != 0) {
            parent.removeTouchDetectionDelegate(this.id)
        }
    }

    /**
     * Automatically inserts the top padding so that whole contents will be visible.
     */
    private fun insertAutoTopPadding() {
        if (0 < width) {
            val slp = heightDiff/width.toFloat()
            var base: View? = null
            for (i in 0 until childCount) {
                if (base != null && base.top > heightDiff) break
                val c = getChildAt(i)
                if (c.top < slp*c.right && (base == null || base.right < c.right)) base = c
            }

            val padding = if(base != null) (slp*base.right - base.top).toInt() else 0
            if (paddingTop < padding) setPadding(paddingStart, padding, paddingEnd, paddingBottom)
        }
    }

    private fun initialize() {
        shapePaint.color = backgroundColor
        // This class only supports the vertical orientation.
        orientation = LinearLayout.VERTICAL
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        // draw shadows
        if (0 < shadowSize) {
            shadowPaint.shader = leftTopCornerShadowShader
            canvas.drawPath(leftTopCornerShadow, shadowPaint)
            shadowPaint.shader = rightTopCornerShadowShader
            canvas.drawPath(rightTopCornerShadow, shadowPaint)
            shadowPaint.shader = topEdgeShadowShader
            canvas.drawPath(topEdgeShadow, shadowPaint)
        }

        // draw shape
        canvas.drawPath(shape, shapePaint)

        if (clipChildren) {
            // clip child views
            canvas.clipPath(shape)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        buildShapeAndShadows()
    }

    private fun buildShapeAndShadows() {
        val r = cornerRadius.toFloat()
        val w = this.width.toFloat()
        val h = this.height.toFloat()
        val d = heightDiff.toFloat()
        val s = shadowSize.toFloat()
        val k = Math.sqrt((d*d + w*w).toDouble()).toFloat()
        val p1 = r
        val q1 = r / w * (d + k)
        val p2 = w - r
        val q2 = q1 + d * (1 - 2 * r / w)
        val z = Math.atan((w / d).toDouble())
        val zdeg = (z * 180 / Math.PI).toFloat()
        val u = (p2 + r * Math.cos(z)).toFloat()
        val v = (q2 - r * Math.sin(z)).toFloat()

        // shape path
        shape.apply {
            reset()
            moveTo(p1-r, q1)
            arcTo(p1-r, q1-r, p1+r, q1+r, 180f, 180f-zdeg, true)
            lineTo(u, v)
            arcTo(p2-r, q2-r, p2+r, q2+r, -zdeg, zdeg, true)
            lineTo(p2+r, h)
            lineTo(p1-r, h)
            lineTo(p1-r, q1)
            close()
        }

        if (shadowSize == 0) return

        // left top corner shadow
        leftTopCornerShadow.apply {
            reset()
            moveTo(p1, q1)
            lineTo(p1-r-s, q1)
            arcTo(p1-r-s, q1-r-s, p1+r+s, q1+r+s, 180f, 180f-zdeg, true)
            lineTo(p1, q1)
            close()
        }

        // right top corner shadow
        rightTopCornerShadow.apply {
            reset()
            moveTo(p2, q2)
            lineTo(p2+r+s, q2)
            arcTo(p2-r-s, q2-r-s, p2+r+s, q2+r+s, 0f, -zdeg, true)
            lineTo(p2, q2)
            close()
        }

        // top edge shadow
        topEdgeShadow.apply {
            reset()
            moveTo(p1, q1)
            lineTo(p1 + (r+s)/k * d, q1 - (r+s)/k * w)
            lineTo(p2 + (r+s)/k * d, q2 - (r+s)/k * w)
            lineTo(p2, q2)
            close()
        }

        // shaders
        val colors = listOf(shadowStartColor, shadowEndColor).toIntArray()
        val points = listOf(r/(r+s), 1f).toFloatArray()

        // left top corner shadow
        leftTopCornerShadowShader = RadialGradient(
            p1, q1, r+s, colors, points, Shader.TileMode.CLAMP)

        // right top corner shadow
        rightTopCornerShadowShader = RadialGradient(
            p2, q2, r+s, colors, points, Shader.TileMode.CLAMP)

        // top edge shadow
        topEdgeShadowShader = LinearGradient(
            w/2 - r/k * d, d/2 + r/k * w,
            w/2 + s/k * d, d/2 - s/k * w,
            colors, points, Shader.TileMode.CLAMP)
    }

    override fun onTouchEvent(event: MotionEvent) = when {
        isPointOutOfBounds(event.x, event.y) -> false
        eatTouchEvent -> true
        else -> super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent) =
        if (isPointOutOfBounds(ev.x, ev.y)) true
        else super.onInterceptTouchEvent(ev)

    private fun isPointOutOfBounds(x: Int, y: Int)
            = isPointOutOfBounds(x.toFloat(), y.toFloat())

    /**
     * (x,y) is a local coordinate.
     */
    private fun isPointOutOfBounds(x: Float, y: Float)
            = !(0..width).contains(x) || y > height || x*heightDiff/width > y

    private fun dpToPx(dp: Int)
            = (context.resources.displayMetrics.density * dp).toInt()

    private class TouchDetectionDelegate(
        override val targetId: Int)
        : SpecialCoordinatorLayout.TouchDetectionDelegate {

        override fun isPointInTargetBounds(target: View, x: Int, y: Int)
                = !(target as SlashLayout).isPointOutOfBounds(x - target.left, y - target.top)
    }
}