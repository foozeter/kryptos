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

    private val _tag = LinkageSlideInOutBehavior::class.java.name

    private val inOutFromLeftSlide: Boolean

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.LinkageSlideInOutBehavior, 0, 0)

        inOutFromLeftSlide = a.getBoolean(
            R.styleable.LinkageSlideInOutBehavior_behavior_slideInOutFromLeftSide, false)

        a.recycle()
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val ret = super.onLayoutChild(parent, child, layoutDirection)
        val bottomSheet = host
        if (bottomSheet != null) configureParams(child, bottomSheet, parent)
        return ret
    }

    private fun configureParams(
        target: View,
        bottomSheet: BottomSheetBehavior<*>,
        parent: CoordinatorLayout) {

        // top of the collapsed bottom sheet
        val tc = parent.height - bottomSheet.peekHeight
        val dx = if (inOutFromLeftSlide) -target.right else parent.width - target.left
        val t = (tc - target.bottom) / tc.toFloat()
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
        } else {
            Log.e(_tag, "the child view is laid out below of collapsed bottom sheet")
        }

    }
}