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

    private val isOverDraggingEnabled: Boolean

    private val defaultState: Int

    private val bottomSheetFader = BottomSheetFader()

    private var userCallback: BottomSheetCallback? = null

    init {
        val a = context.theme.obtainStyledAttributes(
            attributeSet, R.styleable.AdvancedBottomSheetBehavior, 0, 0)

        belowOfOnExpanded = a.getResourceId(
            R.styleable.AdvancedBottomSheetBehavior_absb_belowOfOnExpanded, 0)

        belowOfOnCollapsed = a.getResourceId(
            R.styleable.AdvancedBottomSheetBehavior_absb_belowOfOnCollapsed, 0)

        isOverDraggingEnabled = a.getBoolean(
            R.styleable.AdvancedBottomSheetBehavior_absb_enableOverDragging, false)

        bottomSheetFader.isEnabled = a.getBoolean(
            R.styleable.AdvancedBottomSheetBehavior_absb_fadeOutSheetOnDragging, false)

        bottomSheetFader.collapsedSheetAlpha = a.getFraction(
            R.styleable.AdvancedBottomSheetBehavior_absb_collapsedSheetAlpha, 1, 1, 0.5f)

        defaultState = a.getInt(
            R.styleable.AdvancedBottomSheetBehavior_absb_defaultState,
            BottomSheetBehavior.STATE_COLLAPSED)

        a.recycle()

        super.setBottomSheetCallback(InternalBottomSheetCallback())
        state = defaultState
        if (isOverDraggingEnabled) isHideable = true
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

        bottomSheetFader.applyAlphaForStableState(child, state)

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

    override fun setBottomSheetCallback(callback: BottomSheetCallback?) {
        userCallback = callback
    }

    private inner class InternalBottomSheetCallback: BottomSheetCallback() {

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            bottomSheetFader.onSlide(bottomSheet, slideOffset)
            userCallback?.onSlide(bottomSheet, slideOffset)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            bottomSheetFader.onStateChanged(bottomSheet, newState)
            userCallback?.onStateChanged(bottomSheet, newState)

            if (newState == BottomSheetBehavior.STATE_HIDDEN && isOverDraggingEnabled) {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }
}