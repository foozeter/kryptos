package com.hayashibambi.kryptos.linkagebottomsheetlayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.hayashibambi.kryptos.enhancedbottomsheetbehavior.EnhancedBottomSheetBehavior

class HostBottomSheetBehavior<V: View>(
    context: Context, attrs: AttributeSet?)
    : EnhancedBottomSheetBehavior<V>(context, attrs) {

    private val linkageBehaviors = mutableListOf<LinkageBehavior<*>>()
    private var userCallback: BottomSheetCallback? = null

    internal var view: View? = null; private set

    init {
        super.setBottomSheetCallback(InternalCallback())
    }

    fun addLinkageBehavior(behavior: LinkageBehavior<*>) {
        linkageBehaviors.add(behavior)
        behavior.host = this
    }

    fun removeLinkageBehavior(behavior: LinkageBehavior<*>) {
        linkageBehaviors.remove(behavior)
        behavior.host = null
    }

    internal fun onAttachedToParent(parent: LinkageBottomSheetLayout) {
        val viewId = parent.hostId
        if (viewId != 0) {
            view = parent.findViewById(viewId)
        }
    }

    internal fun onDetachFromParent(parent: LinkageBottomSheetLayout) {
        view = null
    }

    override fun setBottomSheetCallback(callback: BottomSheetCallback?) {
        userCallback = callback
    }

    private inner class InternalCallback: BottomSheetCallback() {

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            linkageBehaviors.forEach { it.onDependencyBottomSheetSlide(bottomSheet, slideOffset) }
            userCallback?.onSlide(bottomSheet, slideOffset)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            linkageBehaviors.forEach { it.onDependencyBottomSheetStateChange(bottomSheet, newState) }
            userCallback?.onStateChanged(bottomSheet, newState)
        }
    }
}