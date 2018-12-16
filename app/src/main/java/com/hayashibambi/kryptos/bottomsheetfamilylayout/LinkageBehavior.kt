package com.hayashibambi.kryptos.bottomsheetfamilylayout

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class LinkageBehavior<V: View>: CoordinatorLayout.Behavior<V>() {

    /**
     * Only HostBottomSheet is allowed to modify this property.
     */
    var host: BottomSheetBehavior<*>? = null
        internal set

    /**
     * Only HostBottomSheet is allowed to modify this property.
     */
    var hostViewId: Int = View.NO_ID
        internal set

    protected var layoutDependsOnHostBottomSheet = false

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View) =
        if (layoutDependsOnHostBottomSheet) dependency.id == hostViewId
        else super.layoutDependsOn(parent, child, dependency)

    abstract fun onDependencyBottomSheetSlide(bottomSheet: View, slideOffset: Float)
    abstract fun onDependencyBottomSheetStateChange(bottomSheet: View, newState: Int)
}