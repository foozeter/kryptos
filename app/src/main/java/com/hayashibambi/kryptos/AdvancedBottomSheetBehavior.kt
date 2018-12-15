package com.hayashibambi.kryptos

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class AdvancedBottomSheetBehavior<V: View>(
    context: Context,
    attributeSet: AttributeSet)
    : BottomSheetBehavior<V>(context, attributeSet) {

    @IdRes
    val belowOfOnExpanded: Int

    @IdRes
    val belowOfOnCollapsed: Int

    init {
        val a = context.theme.obtainStyledAttributes(
            attributeSet, R.styleable.AdvancedBottomSheetBehavior, 0, 0)

        belowOfOnExpanded = a.getResourceId(
            R.styleable.AdvancedBottomSheetBehavior_behavior_belowOfOnExpanded, 0)

        belowOfOnCollapsed = a.getResourceId(
            R.styleable.AdvancedBottomSheetBehavior_behavior_belowOfOnCollapsed, 0)

        a.recycle()
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View)
            = dependency.id == belowOfOnExpanded
            || dependency.id == belowOfOnCollapsed
            || super.layoutDependsOn(parent, child, dependency)

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        var laidOut = false
        if (belowOfOnExpanded != 0) {
            val dependency = parent.findViewById<View>(belowOfOnExpanded)
            val maxHeight = parent.height - dependency.bottom
            if (maxHeight < child.measuredHeight) {
                child.measure(
                    View.MeasureSpec.makeMeasureSpec(child.measuredWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(Math.min(child.measuredHeight, maxHeight), View.MeasureSpec.EXACTLY))

                laidOut = super.onLayoutChild(parent, child, layoutDirection)
            }
        }

        return laidOut
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        var changed = super.onDependentViewChanged(parent, child, dependency)
        if (dependency.id == belowOfOnCollapsed) {
            val peek = parent.height - dependency.bottom
            if (peek != peekHeight) {
                peekHeight = peek
                changed = true
            }
        }

        return changed
    }
}