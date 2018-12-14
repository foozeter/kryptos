package com.hayashibambi.kryptos

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

class SpecialCoordinatorLayout(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int)
    : CoordinatorLayout(context, attrs, defStyleAttr) {

    private val touchDetectionDelegates = mutableMapOf<Int, TouchDetectionDelegate>()

    constructor(
        context: Context,
        attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context): this(context, null, 0)

    override fun isPointInChildBounds(child: View, x: Int, y: Int) =
        touchDetectionDelegates[child.id]?.isPointInTargetBounds(child, x, y)
        ?: super.isPointInChildBounds(child, x, y)

    fun addTouchDetectionDelegate(delegate: TouchDetectionDelegate) {
        touchDetectionDelegates[delegate.targetId] = delegate
    }

    fun removeTouchDetectionDelegate(delegate: TouchDetectionDelegate) {
        touchDetectionDelegates.remove(delegate.targetId)
    }

    fun removeTouchDetectionDelegate(targetId: Int) {
        touchDetectionDelegates.remove(targetId)
    }

    interface TouchDetectionDelegate {
        val targetId: Int
        fun isPointInTargetBounds(target: View, x: Int, y: Int): Boolean
    }
}