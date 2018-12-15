package com.hayashibambi.kryptos

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils

class WhackAMoleBehavior<V: View>(context: Context, attrs: AttributeSet)
    : CoordinatorLayout.Behavior<V>() {

    companion object {
        private const val DEFAULT_REVEAL_FROM_LEFT_SIDE = false
        private const val INVALID_ANCHOR_BOTTOM_SHEET = 0 // invalid res-id
        private const val DEFAULT_DETECTION_RANGE = 128 // dip
        private const val MINIMUM_DETECTION_RANGE = 1 // dip
    }

    private val interpolator: Interpolator = AccelerateDecelerateInterpolator()

    @IdRes
    private val anchorBottomSheet: Int

    private val revealFromLeftSide: Boolean

    @Px
    private val detectionRange: Float

    @Px
    private var motionDistance = 0f

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.WhackAMoleBehavior, 0, 0)

        revealFromLeftSide = a.getBoolean(
            R.styleable.WhackAMoleBehavior_wam_revealFromLeftSide,
            DEFAULT_REVEAL_FROM_LEFT_SIDE)

        anchorBottomSheet = a.getResourceId(
            R.styleable.WhackAMoleBehavior_wam_anchorBottomSheet,
            INVALID_ANCHOR_BOTTOM_SHEET)

        detectionRange = resolveDetectionRange(context, a)

        a.recycle()
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View)
            = dependency.id == anchorBottomSheet

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        if (anchorBottomSheet == 0) return false
        parent.onLayoutChild(child, layoutDirection)
        motionDistance = if (revealFromLeftSide) -child.right.toFloat() else parent.width - child.left.toFloat()
        return true
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        val t = MathUtils.clamp(1f - (dependency.top - child.bottom) / detectionRange, 0f, 1f)
        child.translationX = motionDistance * interpolator.getInterpolation(t)
        return false
    }

    private fun resolveDetectionRange(context: Context, attrs: TypedArray): Float {
        val minRange = dpToPx(MINIMUM_DETECTION_RANGE, context)
        val range = attrs.getDimensionPixelSize(
            R.styleable.WhackAMoleBehavior_wam_detectionRange,
            dpToPx(DEFAULT_DETECTION_RANGE, context))
        return Math.max(range.toFloat(), minRange.toFloat())
    }

    private fun dpToPx(dp: Int, context: Context)
            = (context.resources.displayMetrics.density * dp).toInt()
}