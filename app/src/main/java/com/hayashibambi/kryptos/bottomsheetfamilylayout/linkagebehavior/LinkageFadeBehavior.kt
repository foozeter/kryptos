package com.hayashibambi.kryptos.bottomsheetfamilylayout.linkagebehavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hayashibambi.kryptos.R
import com.hayashibambi.kryptos.bottomsheetfamilylayout.LinkageBehavior

class LinkageFadeBehavior<V: View>(context: Context, attrs: AttributeSet): LinkageBehavior<V>() {

    companion object {
        private const val TAG = "log@LinkageFadeBehavior"
        private const val DEFAULT_ALPHA_ON_DEPENDENCY_EXPANDED = 1f
        private const val DEFAULT_ALPHA_ON_DEPENDENCY_COLLAPSED = 0.5f
        private const val DEFAULT_ALPHA_ON_DEPENDENCY_HIDDEN = 0f
    }

    private val alphaOnDependencyExpanded: Float
    private val alphaOnDependencyCollapsed: Float
    private val alphaOnDependencyHidden: Float

    private var target: View? = null

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.LinkageFadeBehavior, 0, 0)

        alphaOnDependencyExpanded = a.getFraction(
            R.styleable.LinkageFadeBehavior_behavior_alphaOnDependencyExpanded,
            1, 1,
            DEFAULT_ALPHA_ON_DEPENDENCY_EXPANDED
        )

        alphaOnDependencyCollapsed = a.getFraction(
            R.styleable.LinkageFadeBehavior_behavior_alphaOnDependencyCollapsed,
            1, 1,
            DEFAULT_ALPHA_ON_DEPENDENCY_COLLAPSED
        )

        alphaOnDependencyHidden = a.getFraction(
            R.styleable.LinkageFadeBehavior_behavior_alphaOnDependencyHidden,
            1, 1,
            DEFAULT_ALPHA_ON_DEPENDENCY_HIDDEN
        )

        a.recycle()
    }

    override fun onDependencyBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
        target?.alpha = if (0 < slideOffset) {
            alphaOnDependencyCollapsed + (alphaOnDependencyExpanded - alphaOnDependencyCollapsed)* slideOffset
        } else {
            alphaOnDependencyCollapsed + (alphaOnDependencyHidden - alphaOnDependencyCollapsed) * -slideOffset
        }
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val ret = super.onLayoutChild(parent, child, layoutDirection)
        target = child
        if (host != null) applyAlphaForStableState(host!!.state)
        return ret
    }

    override fun onDependencyBottomSheetStateChange(bottomSheet: View, newState: Int) {
        applyAlphaForStableState(newState)
    }


    private fun applyAlphaForStableState(stableState: Int) {
        target?.alpha = when (stableState) {
            BottomSheetBehavior.STATE_EXPANDED -> alphaOnDependencyExpanded
            BottomSheetBehavior.STATE_COLLAPSED -> alphaOnDependencyCollapsed
            BottomSheetBehavior.STATE_HIDDEN -> alphaOnDependencyHidden
            else -> target!!.alpha
        }
    }
}