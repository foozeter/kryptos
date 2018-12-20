package com.hayashibambi.kryptos.ui.linkagebottomsheetlayout.linkagebehavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import androidx.annotation.FloatRange
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hayashibambi.kryptos.R
import com.hayashibambi.kryptos.ui.linkagebottomsheetlayout.LinkageBehavior
import java.lang.ref.WeakReference

abstract class LinkageValueBehavior<V: View>(
    context: Context, attrs: AttributeSet): LinkageBehavior<V>() {
    
    companion object {

        private const val INTERPOLATOR_LINEAR = 1
        private const val INTERPOLATOR_LINEAR_OUT_SLOW_IN = 2
        private const val INTERPOLATOR_ACCELERATE = 3
        private const val INTERPOLATOR_DECELERATE = 4
        private const val INTERPOLATOR_ACCELERATE_DECELERATE = 5
        private const val INTERPOLATOR_ANTICIPATE = 6
        private const val INTERPOLATOR_ANTICIPATE_OVER_SHOOT = 7

        private const val DEFAULT_INTERPOLATOR = INTERPOLATOR_LINEAR
    }

    var valueOnExpanded: Float
    var valueOnCollapsed: Float
    var valueOnHidden: Float

    var interpolator: Interpolator

    private var targetRef: WeakReference<View>? = null

    protected val target; get() = targetRef?.get()


    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.LinkageValueBehavior, 0, 0)

        valueOnExpanded = a.getFloat(
            R.styleable.LinkageValueBehavior_behavior_valueOnExpanded, 0f)

        valueOnCollapsed = a.getFloat(
            R.styleable.LinkageValueBehavior_behavior_valueOnCollapsed, 0f)

        valueOnHidden = a.getFloat(
            R.styleable.LinkageValueBehavior_behavior_valueOnHidden, 0f)

        interpolator = resolveInterpolator(a.getInt(
                R.styleable.LinkageValueBehavior_behavior_valueInterpolator,
                DEFAULT_INTERPOLATOR))

        a.recycle()
    }

    private fun resolveInterpolator(enumValue: Int): Interpolator
            = when (enumValue) {
        INTERPOLATOR_LINEAR -> LinearInterpolator()
        INTERPOLATOR_LINEAR_OUT_SLOW_IN -> LinearOutSlowInInterpolator()
        INTERPOLATOR_ACCELERATE -> AccelerateInterpolator()
        INTERPOLATOR_DECELERATE -> DecelerateInterpolator()
        INTERPOLATOR_ACCELERATE_DECELERATE -> AccelerateDecelerateInterpolator()
        INTERPOLATOR_ANTICIPATE -> AnticipateInterpolator()
        INTERPOLATOR_ANTICIPATE_OVER_SHOOT -> AnticipateOvershootInterpolator()
        else -> throw IllegalArgumentException("unknown enum type")
    }

    override fun onDependencyBottomSheetSlide(bottomSheet: View, slideOffset: Float)
            = applyValue(slideOffset)

    override fun onDependencyBottomSheetStateChange(bottomSheet: View, newState: Int)
            = applyValueForDependencyStableState(newState)

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val ret = super.onLayoutChild(parent, child, layoutDirection)
        targetRef = WeakReference(child)
        // set the initial value
        applyValueForDependencyStableState(host?.state ?: -1)
        return ret
    }

    private fun applyValueForDependencyStableState(stableState: Int) {
        when (stableState) {
            BottomSheetBehavior.STATE_EXPANDED -> applyValue(1f)
            BottomSheetBehavior.STATE_COLLAPSED -> applyValue(0f)
            BottomSheetBehavior.STATE_HIDDEN -> applyValue(-1f)
        }
    }

    private fun applyValue(@FloatRange(from = -1.0, to = 1.0) fraction: Float) {
        val target = target
        if (target != null) {
            var interpolation = interpolator.getInterpolation(Math.abs(fraction))
            if (fraction < 0) interpolation *= -1
            val value = calculateAppliedValue(interpolation)
            onApplyValue(target, value)
        }
    }

    /**
     * @param fraction: interpolated slideOffset of the bottom sheet
     *
     * @return Returned value will be passed into onApplyValue(View, Float) as a second parameter
     */
    open protected fun calculateAppliedValue(@FloatRange(from = -1.0, to = 1.0) fraction: Float): Float =
        if (0 < fraction) valueOnCollapsed + (valueOnExpanded - valueOnCollapsed) * fraction
        else valueOnCollapsed + (valueOnHidden - valueOnCollapsed) * -fraction

    /**
     * @param value: calculated value in LinkageValueBehavior#calculateAppliedValue(Float)
     */
    protected abstract fun onApplyValue(target: View, value: Float)
}