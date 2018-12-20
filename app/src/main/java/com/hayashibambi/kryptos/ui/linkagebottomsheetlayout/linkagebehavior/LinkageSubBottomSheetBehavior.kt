package com.hayashibambi.kryptos.ui.linkagebottomsheetlayout.linkagebehavior

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.hayashibambi.kryptos.R
import com.hayashibambi.kryptos.ui.linkagebottomsheetlayout.LinkageBehavior

class LinkageSubBottomSheetBehavior<V: View>(
    context: Context, attrs: AttributeSet)
    : LinkageBehavior<V>() {

    companion object {
        private const val DEFAULT_SLIDE_OFFSET_THRESHOLD = 0.8f
    }

    var slideOffsetThreshold = DEFAULT_SLIDE_OFFSET_THRESHOLD
        set(value) {
            field = MathUtils.clamp(value, -1f, 1f)
        }

    private val bottomSheetBehavior = BottomSheetBehavior<V>(context, attrs)

    private var savedIsHideable = bottomSheetBehavior.isHideable

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.LinkageSubBottomSheetBehavior, 0, 0)

        slideOffsetThreshold = a.getFloat(
            R.styleable.LinkageSubBottomSheetBehavior_behavior_slideOffsetThreshold,
            slideOffsetThreshold)

        a.recycle()
    }

    override fun onDependencyBottomSheetSlide(bottomSheet: View, slideOffset: Float)
            = onHostSlideOffsetChange(slideOffset)

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val host = host
        if (host != null) when (host.state) {
            BottomSheetBehavior.STATE_EXPANDED -> onHostSlideOffsetChange(1f)
            BottomSheetBehavior.STATE_COLLAPSED -> onHostSlideOffsetChange(0f)
            BottomSheetBehavior.STATE_HIDDEN -> onHostSlideOffsetChange(-1f)
        }

        return bottomSheetBehavior.onLayoutChild(parent, child, layoutDirection)
    }

    private fun onHostSlideOffsetChange(slideOffset: Float) {
        if (slideOffsetThreshold < slideOffset) {
            if (bottomSheetBehavior.state != STATE_COLLAPSED &&
                bottomSheetBehavior.state != STATE_DRAGGING &&
                bottomSheetBehavior.state != STATE_SETTLING) {
                bottomSheetBehavior.state = STATE_COLLAPSED
                bottomSheetBehavior.isHideable = savedIsHideable
            }

        } else {
            if (bottomSheetBehavior.state != STATE_HIDDEN &&
                bottomSheetBehavior.state != STATE_DRAGGING &&
                bottomSheetBehavior.state != STATE_SETTLING) {
                savedIsHideable = bottomSheetBehavior.isHideable
                bottomSheetBehavior.isHideable = true
                bottomSheetBehavior.state = STATE_HIDDEN
            }
        }
    }

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: V)
            = bottomSheetBehavior.onSaveInstanceState(parent, child)

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: V, state: Parcelable)
            = bottomSheetBehavior.onRestoreInstanceState(parent, child, state)

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, ev: MotionEvent)
            = bottomSheetBehavior.onInterceptTouchEvent(parent, child, ev)

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, ev: MotionEvent)
            = bottomSheetBehavior.onTouchEvent(parent, child, ev)

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int)
            = bottomSheetBehavior.onStartNestedScroll(
        coordinatorLayout, child, directTargetChild, target, axes, type)

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int)
            = bottomSheetBehavior.onNestedPreScroll(
        coordinatorLayout, child, target, dx, dy, consumed, type)

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        type: Int)
            = bottomSheetBehavior.onStopNestedScroll(
        coordinatorLayout, child, target, type)

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        velocityX: Float,
        velocityY: Float)
            = bottomSheetBehavior.onNestedPreFling(
        coordinatorLayout, child, target, velocityX, velocityY)


}