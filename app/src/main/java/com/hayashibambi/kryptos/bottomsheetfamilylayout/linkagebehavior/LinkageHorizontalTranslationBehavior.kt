package com.hayashibambi.kryptos.bottomsheetfamilylayout.linkagebehavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

open class LinkageHorizontalTranslationBehavior<V: View>(
    context: Context, attrs: AttributeSet)
    : LinkageTranslationBehavior<V>(context, attrs) {

    final override fun onApplyCoordinate(coordinate: Float, target: View) {
        target.translationX = coordinate
    }

    final override fun onSetPivot(target: View, pivot: Float) {
        target.pivotX = pivot
    }

    final override fun resolveValue(value: Float, valueType: ValueType, target: View, parent: CoordinatorLayout)
            = when (valueType) {
        ValueType.ABSOLUTE -> value
        ValueType.RELATIVE_TO_SELF_SIZE -> target.width * value
        ValueType.RELATIVE_TO_PARENT_SIZE -> parent.width * value
    }

    final override fun resolveCoordinate(
        coordinate: Float,
        coordinateType: CoordinateType,
        target: View,
        parent: CoordinatorLayout) 
            = when (coordinateType) {
        CoordinateType.ABSOLUTE -> coordinate - (target.left + target.width * target.pivotX)
        CoordinateType.RELATIVE_TO_SELF_POSITION -> coordinate
    }
}
