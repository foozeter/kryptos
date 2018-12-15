package com.hayashibambi.kryptos.bottomsheetfamilylayout

import android.content.Context
import android.util.AttributeSet
import android.view.View

class HostBottomSheetBehavior<V: View>(context: Context, attrs: AttributeSet?)
    : com.google.android.material.bottomsheet.BottomSheetBehavior<V>(context, attrs) {

    private val linkageBehaviors = mutableListOf<LinkageBehavior<*>>()
    private var userCallback: BottomSheetCallback? = null

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