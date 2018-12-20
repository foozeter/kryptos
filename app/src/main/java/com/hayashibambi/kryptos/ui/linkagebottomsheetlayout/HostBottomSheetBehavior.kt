package com.hayashibambi.kryptos.ui.linkagebottomsheetlayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.hayashibambi.kryptos.ui.enhancedbottomsheetbehavior.EnhancedBottomSheetBehavior
import java.lang.ref.WeakReference

class HostBottomSheetBehavior<V: View>(
    context: Context, attrs: AttributeSet?)
    : EnhancedBottomSheetBehavior<V>(context, attrs) {

    private val linkageBehaviors = mutableListOf<LinkageBehavior<*>>()
    private var userCallback: BottomSheetCallback? = null
    private var viewRef: WeakReference<View>? = null
    val view; get() = viewRef?.get()

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
            viewRef = WeakReference(parent.findViewById(viewId))
        }
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