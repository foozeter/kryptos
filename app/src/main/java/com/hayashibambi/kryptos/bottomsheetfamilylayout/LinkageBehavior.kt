package com.hayashibambi.kryptos.bottomsheetfamilylayout

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
abstract class LinkageBehavior<V: View>: CoordinatorLayout.Behavior<V>() {

    /**
     * Only HostBottomSheet is allowed to modify this property.
     */
    var host: HostBottomSheetBehavior<*>? = null; internal set

    protected val hostView: View?; get() = host?.view

    abstract fun onDependencyBottomSheetSlide(bottomSheet: View, slideOffset: Float)
    abstract fun onDependencyBottomSheetStateChange(bottomSheet: View, newState: Int)
}