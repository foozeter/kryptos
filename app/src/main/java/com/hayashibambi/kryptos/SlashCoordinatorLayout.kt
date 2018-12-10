package com.hayashibambi.kryptos

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

class SlashCoordinatorLayout(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int)
    : CoordinatorLayout(context, attrs, defStyleAttr) {

    constructor(
        context: Context,
        attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context): this(context, null, 0)

    /**
     * In case of using SlashLayout as a bottom sheet with NORMAL CoordinatorLayout,
     * it will be dragged even if the touch position is in the actual bounds of the sheet,
     * but not in the visual bounds of it. This is unexpected behavior.
     * That's why we use this SPECIAL CoordinatorLayout.
     *
     * In BottomSheetBehavior#onInterceptTouchEvent(), the BottomSheetBehavior calls
     * CoordinatorLayout#isPointInChildBounds() to determine whether to drag the sheet or not.
     * So we override this method and return TRUE only when the touch point is in the visual bounds of the sheet.
     */
    override fun isPointInChildBounds(child: View, x: Int, y: Int) =
        if (child is SlashLayout) !child.isPointOutOfBounds(x-child.left, y-child.top)
        else super.isPointInChildBounds(child, x, y)
}