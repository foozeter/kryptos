package com.hayashibambi.kryptos.bottomsheetfamilylayout.linkagebehavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hayashibambi.kryptos.R
import com.hayashibambi.kryptos.bottomsheetfamilylayout.LinkageBehavior

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
        private const val DEFAULT_VALUE = 0f
    }

    private var target: View? = null
    
    private val valueOnDependencyExpanded: Float
    private val valueOnDependencyCollapsed: Float
    private val valueOnDependencyHidden: Float

    private val interpolator: Interpolator
    
    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.LinkageValueBehavior, 0, 0)

        valueOnDependencyExpanded = a.getFloat(
            R.styleable.LinkageValueBehavior_behavior_valueOnDependencyExpanded,
            DEFAULT_VALUE)

        valueOnDependencyCollapsed = a.getFloat(
            R.styleable.LinkageValueBehavior_behavior_valueOnDependencyCollapsed,
            DEFAULT_VALUE)

        valueOnDependencyHidden = a.getFloat(
            R.styleable.LinkageValueBehavior_behavior_valueOnDependencyHidden,
            DEFAULT_VALUE)

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

    override fun onDependencyBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
        val value =
            if (0 < slideOffset) (valueOnDependencyCollapsed
                        + (valueOnDependencyExpanded - valueOnDependencyCollapsed)
                        * interpolator.getInterpolation(slideOffset))

            else (valueOnDependencyCollapsed
                    + (valueOnDependencyHidden - valueOnDependencyCollapsed)
                    * -interpolator.getInterpolation(slideOffset))

        supplyInterpolatedValueToTarget(value)
    }

    override fun onDependencyBottomSheetStateChange(bottomSheet: View, newState: Int) {
        supplyValueToTargetForStableDependencyState(newState)
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val ret = super.onLayoutChild(parent, child, layoutDirection)
        target = child
        // set the initial value
        supplyValueToTargetForStableDependencyState(host?.state ?: -1)
        return ret
    }

    override fun onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams()
        target = null
    }

    private fun supplyValueToTargetForStableDependencyState(stableState: Int) {
        when (stableState) {
            BottomSheetBehavior.STATE_EXPANDED -> supplyInterpolatedValueToTarget(valueOnDependencyExpanded)
            BottomSheetBehavior.STATE_COLLAPSED -> supplyInterpolatedValueToTarget(valueOnDependencyCollapsed)
            BottomSheetBehavior.STATE_HIDDEN -> supplyInterpolatedValueToTarget(valueOnDependencyHidden)
        }
    }

    private fun supplyInterpolatedValueToTarget(value: Float) {
        val target = target
        if (target != null) onSupplyInterpolatedValue(target, value)
    }

    protected abstract fun onSupplyInterpolatedValue(target: View, value: Float)
}