package com.hayashibambi.kryptos.bottomsheetfamilylayout

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class LinkageBehavior<V: View>: CoordinatorLayout.Behavior<V>() {

    /**
     * Only HostBottomSheet is allowed to assign a value to this property.
     */
    var host: BottomSheetBehavior<*>? = null
        internal set

    abstract fun onDependencyBottomSheetSlide(bottomSheet: View, slideOffset: Float)
    abstract fun onDependencyBottomSheetStateChange(bottomSheet: View, newState: Int)
}