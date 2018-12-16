package com.hayashibambi.kryptos.bottomsheetfamilylayout.linkagebehavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hayashibambi.kryptos.R
import com.hayashibambi.kryptos.bottomsheetfamilylayout.LinkageBehavior

open class LinkageFractionBehavior<V: View>(
    context: Context, attrs: AttributeSet): LinkageBehavior<V>() {
    
    companion object {
        private const val DEFAULT_FRACTION_ON_DEPENDENCY_EXPANDED = 1f
        private const val DEFAULT_FRACTION_ON_DEPENDENCY_COLLAPSED = 0f
        private const val DEFAULT_FRACTION_ON_DEPENDENCY_HIDDEN = 0f
    }

    private var target: View? = null
    
    private val fractionOnDependencyExpanded: Float
    private val fractionOnDependencyCollapsed: Float
    private val fractionOnDependencyHidden: Float
    
    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.LinkageFractionBehavior, 0, 0)

        fractionOnDependencyExpanded = a.getFraction(
            R.styleable.LinkageFractionBehavior_behavior_fractionOnDependencyExpanded,
            1, 1, DEFAULT_FRACTION_ON_DEPENDENCY_EXPANDED)

        fractionOnDependencyCollapsed = a.getFraction(
            R.styleable.LinkageFractionBehavior_behavior_fractionOnDependencyCollapsed,
            1, 1, DEFAULT_FRACTION_ON_DEPENDENCY_COLLAPSED)

        fractionOnDependencyHidden = a.getFraction(
            R.styleable.LinkageFractionBehavior_behavior_fractionOnDependencyHidden,
            1, 1, DEFAULT_FRACTION_ON_DEPENDENCY_HIDDEN)

        a.recycle()
    }

    override fun onDependencyBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
        val fraction = if (0 < slideOffset) {
            fractionOnDependencyCollapsed + (fractionOnDependencyExpanded - fractionOnDependencyCollapsed)* slideOffset
        } else {
            fractionOnDependencyCollapsed + (fractionOnDependencyHidden - fractionOnDependencyCollapsed) * -slideOffset
        }

        supplyFractionToTarget(fraction)
    }

    override fun onDependencyBottomSheetStateChange(bottomSheet: View, newState: Int) {
        supplyFractionToTargetForStableDependencyState(newState)
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val ret = super.onLayoutChild(parent, child, layoutDirection)
        target = child
        // set the initial fraction
        supplyFractionToTargetForStableDependencyState(host?.state ?: -1)
        return ret
    }

    override fun onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams()
        target = null
    }

    private fun supplyFractionToTargetForStableDependencyState(stableState: Int) {
        when (stableState) {
            BottomSheetBehavior.STATE_EXPANDED -> supplyFractionToTarget(fractionOnDependencyExpanded)
            BottomSheetBehavior.STATE_COLLAPSED -> supplyFractionToTarget(fractionOnDependencyCollapsed)
            BottomSheetBehavior.STATE_HIDDEN -> supplyFractionToTarget(fractionOnDependencyHidden)
        }
    }

    private fun supplyFractionToTarget(fraction: Float) {
        val target = target
        if (target != null) onSupplyFraction(target, fraction)
    }

    protected open fun onSupplyFraction(target: View, fraction: Float) {
        if (target is FractionConsumer) target.supplyFraction(fraction)
    }

    interface FractionConsumer {
        fun supplyFraction(fraction: Float)
    }
}