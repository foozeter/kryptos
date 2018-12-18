package com.hayashibambi.kryptos.linkagebottomsheetlayout.linkagebehavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hayashibambi.kryptos.R

class LinkageSlideInOutBehavior<V: View>(
    context: Context, attrs: AttributeSet)
    : LinkageHorizontalTranslationBehavior<V>(context, attrs) {

    private val _tag = LinkageSlideInOutBehavior::class.java.simpleName

    private val inOutFromLeftSlide: Boolean

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.LinkageSlideInOutBehavior, 0, 0)

        inOutFromLeftSlide = a.getBoolean(
            R.styleable.LinkageSlideInOutBehavior_behavior_slideInOutFromLeftSide, false)

        a.recycle()
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View)
            = dependency.id == hostView?.id || super.layoutDependsOn(parent, child, dependency)

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        super.onLayoutChild(parent, child, layoutDirection)
        parent.onLayoutChild(child, layoutDirection)
        val bottomSheet = host
        val bottomSheetView = hostView
        if (bottomSheet != null && bottomSheetView != null) {
            configureParams(child, bottomSheet, bottomSheetView, parent)
        }

        return true
    }

    override fun onDependencyBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
        super.onDependencyBottomSheetSlide(bottomSheet, slideOffset)
    }

    private fun configureParams(
        target: View,
        bottomSheet: BottomSheetBehavior<*>,
        bottomSheetView: View,
        parent: CoordinatorLayout) {

        // peekHeight is -1 by default
        if (bottomSheet.peekHeight == -1) {
            Log.w(_tag, "specify 'app:behavior_peekHeight' " +
                    "to make this behavior work properly!")
        }

        // top of the collapsed bottom sheet
        val tc = parent.height - bottomSheet.peekHeight
        // top of the expanded bottom sheet
        val te = parent.height - bottomSheetView.height
        val dx = if (inOutFromLeftSlide) -target.right else parent.width - target.left
        val t = (tc - target.bottom) / (tc - te).toFloat()
        val it = interpolator.getInterpolation(t)

        if (0 < it) {
            pivot = if (inOutFromLeftSlide) 1f else 0f
            valueOnCollapsed = 0f
            valueOnExpanded = dx / it
            valueTypeOnCollapsed = ValueType.ABSOLUTE
            valueTypeOnExpanded = ValueType.ABSOLUTE
            coordinateTypeOnCollapsed = CoordinateType.RELATIVE_TO_SELF_POSITION
            coordinateTypeOnExpanded = CoordinateType.RELATIVE_TO_SELF_POSITION

            valueOnHidden = valueOnCollapsed
            valueTypeOnHidden = valueTypeOnCollapsed
            coordinateTypeOnHidden = coordinateTypeOnCollapsed
        }
    }
}