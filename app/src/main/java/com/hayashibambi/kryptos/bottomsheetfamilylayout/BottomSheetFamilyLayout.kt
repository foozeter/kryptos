package com.hayashibambi.kryptos.bottomsheetfamilylayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.hayashibambi.kryptos.R

class BottomSheetFamilyLayout(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int)
    : CoordinatorLayout(context, attrs, defStyleAttr) {

    @IdRes
    internal val hostId: Int

    private var hostBehavior: HostBottomSheetBehavior<*>? = null

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.BottomSheetFamilyLayout, 0, 0)

        hostId = a.getResourceId(
            R.styleable.BottomSheetFamilyLayout_layout_hostBottomSheet,
            View.NO_ID)

        a.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (hostId != View.NO_ID) {
            val sheet = findViewById<View>(hostId)
            val behavior = (sheet.layoutParams as CoordinatorLayout.LayoutParams).behavior
            if (behavior is HostBottomSheetBehavior) {
                hostBehavior = behavior
                hostBehavior?.onAttachedToParent(this)
            }
        }

        if (hostBehavior != null) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child.id != hostId) {
                    val behavior = (child.layoutParams as CoordinatorLayout.LayoutParams).behavior
                    if (behavior is LinkageBehavior) {
                        hostBehavior!!.addLinkageBehavior(behavior)
                    }
                }
            }
        }
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        val behavior = (child.layoutParams as CoordinatorLayout.LayoutParams).behavior
        if (behavior is LinkageBehavior) {
            hostBehavior?.addLinkageBehavior(behavior)
        }
    }

    override fun onViewRemoved(child: View) {
        super.onViewRemoved(child)
        val behavior = (child.layoutParams as CoordinatorLayout.LayoutParams).behavior
        if (behavior is LinkageBehavior) {
            hostBehavior?.removeLinkageBehavior(behavior)
        } else if (behavior is HostBottomSheetBehavior<*>) {
            hostBehavior?.onDetachFromParent(this)
            hostBehavior = null
        }
    }
}