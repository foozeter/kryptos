package com.hayashibambi.kryptos.ui.linkagebottomsheetlayout.linkagebehavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

class LinkageVerticalTranslationBehavior<V: View>(
    context: Context, attrs: AttributeSet)
    : LinkageTranslationBehavior<V>(context, attrs) {

    override fun onApplyCoordinate(coordinate: Float, target: View) {
        target.translationY = coordinate
    }

    override fun onSetPivot(target: View, pivot: Float) {
        target.pivotY = pivot
    }

    override fun resolveValue(value: Float, valueType: ValueType, target: View, parent: CoordinatorLayout)
            = when (valueType) {
        ValueType.ABSOLUTE -> value
        ValueType.RELATIVE_TO_SELF_SIZE -> target.height * value
        ValueType.RELATIVE_TO_PARENT_SIZE -> parent.height * value
    }

    override fun resolveCoordinate(
        coordinate: Float,
        coordinateType: CoordinateType,
        target: View,
        parent: CoordinatorLayout
    )
            = when (coordinateType) {
        CoordinateType.ABSOLUTE -> coordinate - (target.top + target.height * target.pivotY)
        CoordinateType.RELATIVE_TO_SELF_POSITION -> coordinate
    }
}
