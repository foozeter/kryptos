package com.hayashibambi.kryptos

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetBehaviorWithSlashLayout<V: View>
    (context: Context, attrs: AttributeSet?)
    : BottomSheetBehavior<V>(context, attrs), SlashLayout.OnTouchEventOutOfBoundsListener {

    private var target: SlashLayout? = null
        set(value) {
            field?.setOnTouchEventOutOfBoundsListener(null)
            value?.setOnTouchEventOutOfBoundsListener(this)
            field = value
        }

    constructor(context: Context): this(context, null)

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        if (target == null && child is SlashLayout) target = child
        Log.d("mylog", "onIntercept")
        return if (event.x == 0f && event.y == 0f) super.onInterceptTouchEvent(parent, child, event)
        else if (child is SlashLayout) !child.isAbsPointOutOfBounds(event.x, event.y)
        else super.onInterceptTouchEvent(parent, child, event)
//        return super.onInterceptTouchEvent(parent, child, event)
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        Log.d("mylog", "onTouch")
//        return if (event.x == 0f && event.y == 0f) false
//        else if (child is SlashLayout) child.isAbsPointOutOfBounds(event.x, event.y)
//        else super.onTouchEvent(parent, child, event)
        return super.onTouchEvent(parent, child, event)
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        Log.d("mylog", "onStartNestedScroll")
//        return false
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
//        Log.d("mylog", "onNestedPreScroll")
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, type: Int) {
        Log.d("mylog", "onStopNestedScroll")
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.d("mylog", "onNestedPreFling")
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
//        return false
    }

    override fun onTouchEventOutOfBounds(pointer: Int, ev: MotionEvent): Boolean {
        Log.d("mylog", "######################################################")
        return true
    }
}
