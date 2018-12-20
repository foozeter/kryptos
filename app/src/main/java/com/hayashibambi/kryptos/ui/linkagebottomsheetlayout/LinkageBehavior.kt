package com.hayashibambi.kryptos.ui.linkagebottomsheetlayout

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
abstract class LinkageBehavior<V: View>: CoordinatorLayout.Behavior<V>() {

    /**
     * Only HostBottomSheet is allowed to modify this property.
     */
    var host: HostBottomSheetBehavior<*>? = null; internal set

    protected val hostView: View?; get() = host?.view

    open fun onDependencyBottomSheetSlide(bottomSheet: View, slideOffset: Float) {}
    open fun onDependencyBottomSheetStateChange(bottomSheet: View, newState: Int) {}
}