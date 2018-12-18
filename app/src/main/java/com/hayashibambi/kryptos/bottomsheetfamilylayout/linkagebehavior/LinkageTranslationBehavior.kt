package com.hayashibambi.kryptos.bottomsheetfamilylayout.linkagebehavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.hayashibambi.kryptos.R

abstract class LinkageTranslationBehavior<V: View>(
    context: Context, attrs: AttributeSet)
    : LinkageValueBehavior<V>(context, attrs) {

    companion object {
        private const val PIVOT_NOT_SET = -1f
        private const val DEFAULT_VALUE_TYPE = 0
        private const val DEFAULT_COORDINATE_TYPE = 0
    }

    enum class ValueType(private val value: Int) {
        ABSOLUTE(0),
        RELATIVE_TO_SELF_SIZE(1),
        RELATIVE_TO_PARENT_SIZE(2);

        companion object {
            private val map = ValueType.values().associateBy(ValueType::value)
            fun from(value: Int) = map[value]
        }
    }

    enum class CoordinateType(private val value: Int) {
        ABSOLUTE(0),
        RELATIVE_TO_SELF_POSITION(1);

        companion object {
            private val map = CoordinateType.values().associateBy(CoordinateType::value)
            fun from(value: Int) = map[value]
        }
    }

    var pivot: Float

    var coordinateTypeOnExpanded: CoordinateType
    var coordinateTypeOnCollapsed: CoordinateType
    var coordinateTypeOnHidden: CoordinateType
    var valueTypeOnExpanded: ValueType
    var valueTypeOnCollapsed: ValueType
    var valueTypeOnHidden: ValueType

    private var parent: CoordinatorLayout? = null

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.LinkageTranslationBehavior, 0, 0)

        pivot = a.getFloat(
            R.styleable.LinkageTranslationBehavior_behavior_pivot,
            PIVOT_NOT_SET)

        coordinateTypeOnExpanded = resolveCoordinateType(a.getInt(
            R.styleable.LinkageTranslationBehavior_behavior_coordinateTypeOnExpanded,
            DEFAULT_COORDINATE_TYPE))

        coordinateTypeOnCollapsed = resolveCoordinateType(a.getInt(
            R.styleable.LinkageTranslationBehavior_behavior_coordinateTypeOnCollapsed,
            DEFAULT_COORDINATE_TYPE))

        coordinateTypeOnHidden = resolveCoordinateType(a.getInt(
            R.styleable.LinkageTranslationBehavior_behavior_coordinateTypeOnHidden,
            DEFAULT_COORDINATE_TYPE))

        valueTypeOnExpanded = resolveValueType(a.getInt(
            R.styleable.LinkageTranslationBehavior_behavior_valueTypeOnExpanded,
            DEFAULT_VALUE_TYPE))

        valueTypeOnCollapsed = resolveValueType(a.getInt(
            R.styleable.LinkageTranslationBehavior_behavior_valueTypeOnCollapsed,
            DEFAULT_VALUE_TYPE))

        valueTypeOnHidden = resolveValueType(a.getInt(
            R.styleable.LinkageTranslationBehavior_behavior_valueTypeOnHidden,
            DEFAULT_VALUE_TYPE))

        a.recycle()
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        this.parent = parent
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams()
        parent = null
    }

    override fun onApplyValue(target: View, value: Float) {
        val parent = parent
        if (parent != null) {
            if (pivot != PIVOT_NOT_SET) onSetPivot(target, pivot)
            onApplyCoordinate(value, target)
        }
    }

    override fun calculateAppliedValue(fraction: Float): Float {
        val target = target
        val parent = parent
        target ?: return 0f
        parent ?: return 0f

        return if (0 < fraction) {
            val coc = resolveCoordinate(
                resolveValue(valueOnCollapsed, valueTypeOnCollapsed, target, parent),
                coordinateTypeOnCollapsed, target, parent)
            val coe = resolveCoordinate(
                resolveValue(valueOnExpanded, valueTypeOnExpanded, target, parent),
                coordinateTypeOnExpanded, target, parent)

            coc + (coe - coc) * fraction

        } else {
            val coc = resolveCoordinate(
                resolveValue(valueOnCollapsed, valueTypeOnCollapsed, target, parent),
                coordinateTypeOnCollapsed, target, parent)
            val coh = resolveCoordinate(
                resolveValue(valueOnHidden, valueTypeOnHidden, target, parent),
                coordinateTypeOnHidden, target, parent)

            coc + (coh - coc) * -fraction
        }
    }

    private fun resolveValueType(value: Int)
            = ValueType.from(value) ?: throw IllegalArgumentException("unknown type: $value")

    private fun resolveCoordinateType(value: Int)
            = CoordinateType.from(value) ?: throw IllegalArgumentException("unknown type: $value")

    protected abstract fun onSetPivot(target: View, pivot: Float)

    protected abstract fun onApplyCoordinate(coordinate: Float, target: View)

    protected abstract fun resolveValue(value: Float, valueType: ValueType,
                                        target: View, parent: CoordinatorLayout): Float

    protected abstract fun resolveCoordinate(coordinate: Float, coordinateType: CoordinateType,
                                             target: View, parent: CoordinatorLayout): Float
}