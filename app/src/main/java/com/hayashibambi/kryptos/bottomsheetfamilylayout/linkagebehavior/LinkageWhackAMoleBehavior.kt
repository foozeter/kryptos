package com.hayashibambi.kryptos.bottomsheetfamilylayout.linkagebehavior

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.annotation.Px
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import com.hayashibambi.kryptos.R
import com.hayashibambi.kryptos.bottomsheetfamilylayout.LinkageBehavior

class LinkageWhackAMoleBehavior<V: View>(context: Context, attrs: AttributeSet) : LinkageBehavior<V>() {

    companion object {
        private const val DEFAULT_REVEAL_FROM_LEFT_SIDE = false
        private const val DEFAULT_DETECTION_RANGE = 128 // dip
        private const val MINIMUM_DETECTION_RANGE = 1 // dip
    }

    private val interpolator: Interpolator = AccelerateDecelerateInterpolator()

    private val revealFromLeftSide: Boolean

    @Px
    private val detectionRange: Float

    @Px
    private var motionDistance = 0f

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.LinkageWhackAMoleBehavior, 0, 0)

        revealFromLeftSide = a.getBoolean(
            R.styleable.LinkageWhackAMoleBehavior_behavior_revealFromLeftSide,
            DEFAULT_REVEAL_FROM_LEFT_SIDE)

        detectionRange = resolveDetectionRange(context, a)

        a.recycle()

        layoutDependsOnHostBottomSheet = true
    }

    override fun onDependencyBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
        // do nothing.
    }

    override fun onDependencyBottomSheetStateChange(bottomSheet: View, newState: Int) {
        // do nothing.
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
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
            R.styleable.LinkageWhackAMoleBehavior_behavior_detectionRange,
            dpToPx(DEFAULT_DETECTION_RANGE, context))
        return Math.max(range.toFloat(), minRange.toFloat())
    }

    private fun dpToPx(dp: Int, context: Context)
            = (context.resources.displayMetrics.density * dp).toInt()
}