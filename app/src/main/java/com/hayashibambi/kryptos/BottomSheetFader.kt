package com.hayashibambi.kryptos

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

open class BottomSheetFader: BottomSheetBehavior.BottomSheetCallback() {

    var isEnabled = true

    var collapsedSheetAlpha = 0.5f

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        if (isEnabled) {
            bottomSheet.alpha =
                    if (slideOffset < 0) collapsedSheetAlpha * (1 + slideOffset)
                    else collapsedSheetAlpha + (1f - collapsedSheetAlpha) * slideOffset
        }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        // do noting
    }

    fun applyAlphaForStableState(bottomSheet: View, stableState: Int) {
        bottomSheet.alpha = when (stableState) {
            BottomSheetBehavior.STATE_EXPANDED -> 1f
            BottomSheetBehavior.STATE_COLLAPSED -> collapsedSheetAlpha
            BottomSheetBehavior.STATE_HIDDEN -> 0f
            else -> bottomSheet.alpha
        }
    }
}