package com.hayashibambi.kryptos

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior

class EnhancedBottomSheetBehavior<V: View>(
    context: Context,
    attributeSet: AttributeSet)
    : BottomSheetBehavior<V>(context, attributeSet) {

    companion object {
        private const val KEY_USER_CALLBACK = 0
        private const val KEY_PLUGIN_MANAGER = 1
        private const val KEY_PLUGIN_DEFAULT_STATE = 2
        private const val KEY_PLUGIN_OVER_DRAGGING = 3
        private const val KEY_PLUGIN_FADE_ON_SLIDING = 4
        private const val KEY_PLUGIN_LAYOUT_BELOW_OF = 5
        private const val KEY_PLUGIN_PEEK_BELOW_OF = 6
    }

    private val callbackDispatcher = BottomSheetCallbackDispatcher()
    
    private val pluginManager = PluginManager()

    init {

        // plugins
        val defaultState = DefaultState()
        val overDragging = OverDragging()
        val fadeOnSliding = FadeOnSliding()
        val layoutBelowOf = LayoutBelowOf()
        val peekBelowOf = PeekBelowOf()

        var enableDefaultState = false
        var enableOverDragging = false
        var enableFadeOnSliding = false
        var enableLayoutBelowOf = false
        var enablePeekBelowOf = false

        val a = context.theme.obtainStyledAttributes(
            attributeSet, R.styleable.EnhancedBottomSheetBehavior, 0, 0)

        defaultState.apply {
            val defState = a.getInt(
                R.styleable.EnhancedBottomSheetBehavior_behavior_defaultState, -1)
            if (defState != -1) {
                this.defaultState = defState
                enableDefaultState = true
            }
        }

        fadeOnSliding.apply {
            val alpha = a.getFloat(
                R.styleable.EnhancedBottomSheetBehavior_behavior_collapsedSheetAlpha, -1f)
            if (alpha != -1f) {
                this.collapsedSheetAlpha = alpha
                enableFadeOnSliding = true
            }
        }

        layoutBelowOf.apply {
            this.dependencyId = a.getResourceId(
                R.styleable.EnhancedBottomSheetBehavior_behavior_layoutBelowOf, View.NO_ID)
            if (dependencyId != View.NO_ID) enableLayoutBelowOf = true
        }

        peekBelowOf.apply {
            this.dependencyId = a.getResourceId(
                R.styleable.EnhancedBottomSheetBehavior_behavior_peekBelowOf, View.NO_ID)
            if (dependencyId != View.NO_ID) enablePeekBelowOf = true
        }

        enableDefaultState = a.getBoolean(
            R.styleable.EnhancedBottomSheetBehavior_behavior_enableDefaultState,
            enableDefaultState)

        enableOverDragging = a.getBoolean(
            R.styleable.EnhancedBottomSheetBehavior_behavior_enableOverDragging,
            enableOverDragging)

        enableFadeOnSliding = a.getBoolean(
            R.styleable.EnhancedBottomSheetBehavior_behavior_enableFadeOnScrolling,
            enableFadeOnSliding)

        enableLayoutBelowOf = a.getBoolean(
            R.styleable.EnhancedBottomSheetBehavior_behavior_enableLayoutBelowOf,
            enableLayoutBelowOf)

        enablePeekBelowOf = a.getBoolean(
            R.styleable.EnhancedBottomSheetBehavior_behavior_enablePeekBelowOf,
            enablePeekBelowOf)

        a.recycle()

        callbackDispatcher.listeners[KEY_PLUGIN_MANAGER] = pluginManager
        super.setBottomSheetCallback(callbackDispatcher)

        pluginManager.listeners.apply {
            if(enableDefaultState) put(KEY_PLUGIN_DEFAULT_STATE, defaultState)
            if (enableOverDragging) put(KEY_PLUGIN_OVER_DRAGGING, overDragging)
            if (enableFadeOnSliding) put(KEY_PLUGIN_FADE_ON_SLIDING, fadeOnSliding)
            if (enableLayoutBelowOf) put(KEY_PLUGIN_LAYOUT_BELOW_OF, layoutBelowOf)
            if (enablePeekBelowOf) put(KEY_PLUGIN_PEEK_BELOW_OF, peekBelowOf)
        }

        pluginManager.dispatchOnInit(behavior = this)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View)
            = pluginManager.isAnyPluginLayoutDependingOn(parent, child, dependency)
            || super.layoutDependsOn(parent, child, dependency)

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        pluginManager.dispatchOnBeforeLayout(parent, this, child)
        val laidOut = super.onLayoutChild(parent, child, layoutDirection)
        if (laidOut) pluginManager.dispatchOnLayoutFinish(parent, this, child)
        return laidOut
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        var changed = super.onDependentViewChanged(parent, child, dependency)
        changed = changed.or(pluginManager.dispatchOnDependentViewChanged(parent, child, dependency, this))
        return changed
    }

    override fun setBottomSheetCallback(callback: BottomSheetCallback?) {
        if (callback == null) callbackDispatcher.listeners.remove(KEY_USER_CALLBACK)
        else callbackDispatcher.listeners[KEY_USER_CALLBACK] = callback
    }

    private open class ABSBottomSheetCallbackDispatcher<T: BottomSheetCallback>
        : BottomSheetCallback() {

        val listeners = mutableMapOf<Int, T>()

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            listeners.forEach { it.value.onSlide(bottomSheet, slideOffset) }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            listeners.forEach { it.value.onStateChanged(bottomSheet, newState) }
        }
    }
    
    private class BottomSheetCallbackDispatcher
        : ABSBottomSheetCallbackDispatcher<BottomSheetCallback>()

    private class PluginManager
        : ABSBottomSheetCallbackDispatcher<Plugin>() {

        fun isAnyPluginLayoutDependingOn(
            parent: CoordinatorLayout,
            bottomSheet: View,
            dependency: View): Boolean {
            var ret = false
            listeners.forEach { ret = ret.or(it.value.isLayoutDependingOn(parent, bottomSheet, dependency)) }
            return ret
        }
        
        fun dispatchOnInit(behavior: BottomSheetBehavior<*>) {
            listeners.forEach { it.value.onInit(behavior) }
        }

        fun dispatchOnBeforeLayout(
            parent: CoordinatorLayout,
            behavior: BottomSheetBehavior<*>,
            bottomSheet: View) {
            listeners.forEach { it.value.onBeforeLayout(parent, behavior, bottomSheet) }
        }

        fun dispatchOnLayoutFinish(parent: CoordinatorLayout, behavior: BottomSheetBehavior<*>, bottomSheet: View) {
            listeners.forEach { it.value.onLayoutFinish(parent, behavior, bottomSheet) }
        }

        fun dispatchOnDependentViewChanged(
            parent: CoordinatorLayout,
            bottomSheet: View,
            dependency: View,
            behavior: BottomSheetBehavior<*>): Boolean {
            var ret = false
            listeners.forEach {
                ret = ret.or(it.value.onDependencyViewChanged(
                    parent, bottomSheet, dependency, behavior))
            }

            return ret
        }
    }

    private abstract class Plugin 
        : BottomSheetCallback() {

        open fun isLayoutDependingOn(
            parent: CoordinatorLayout,
            bottomSheet: View,
            dependency: View) = false
        
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        override fun onStateChanged(bottomSheet: View, newState: Int) {}

        open fun onInit(behavior: BottomSheetBehavior<*>) {}

        open fun onBeforeLayout(
            parent: CoordinatorLayout,
            behavior: BottomSheetBehavior<*>,
            bottomSheet: View) {}

        open fun onLayoutFinish(
            parent: CoordinatorLayout,
            behavior: BottomSheetBehavior<*>,
            bottomSheet: View) {}

        open fun onDependencyViewChanged(
            parent: CoordinatorLayout,
            bottomSheet: View,
            dependency: View,
            behavior: BottomSheetBehavior<*>) = false
    }
    
    private class DefaultState: Plugin() {
        
        var defaultState: Int = BottomSheetBehavior.STATE_COLLAPSED

        override fun onInit(behavior: BottomSheetBehavior<*>) {
            behavior.state = defaultState
        }
    }

    private class OverDragging: Plugin() {

        private var behavior: BottomSheetBehavior<*>? = null

        override fun onInit(behavior: BottomSheetBehavior<*>) {
            this.behavior = behavior
            behavior.isHideable = true
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private class FadeOnSliding: Plugin() {

        var collapsedSheetAlpha = 0.5f
            set(value) {
                field = MathUtils.clamp(value, 0f, 1f)
            }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            bottomSheet.alpha =
                    if (slideOffset < 0) collapsedSheetAlpha * (1 + slideOffset)
                    else collapsedSheetAlpha + (1f - collapsedSheetAlpha) * slideOffset
        }

        override fun onLayoutFinish(parent: CoordinatorLayout, behavior: BottomSheetBehavior<*>, bottomSheet: View) {
            bottomSheet.alpha = when (behavior.state) {
                BottomSheetBehavior.STATE_EXPANDED -> 1f
                BottomSheetBehavior.STATE_COLLAPSED -> collapsedSheetAlpha
                BottomSheetBehavior.STATE_HIDDEN -> 0f
                else -> bottomSheet.alpha
            }
        }
    }

    private class LayoutBelowOf: Plugin() {

        @IdRes
        var dependencyId = View.NO_ID

        override fun isLayoutDependingOn(
            parent: CoordinatorLayout,
            bottomSheet: View,
            dependency: View) = dependency.id == dependencyId

        override fun onBeforeLayout(
            parent: CoordinatorLayout,
            behavior: BottomSheetBehavior<*>,
            bottomSheet: View) {
            if (dependencyId != View.NO_ID) {
                val dependency = parent.findViewById<View>(dependencyId)
                val maxHeight = parent.height - dependency.bottom
                if (maxHeight < bottomSheet.measuredHeight) {
                    bottomSheet.measure(
                        View.MeasureSpec.makeMeasureSpec(bottomSheet.measuredWidth, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(
                            Math.min(bottomSheet.measuredHeight, maxHeight),
                            View.MeasureSpec.EXACTLY
                        )
                    )
                }
            }
        }
    }

    private class PeekBelowOf: Plugin() {

        @IdRes
        var dependencyId = View.NO_ID

        override fun isLayoutDependingOn(
            parent: CoordinatorLayout,
            bottomSheet: View,
            dependency: View) = dependency.id == dependencyId

        override fun onDependencyViewChanged(
            parent: CoordinatorLayout,
            bottomSheet: View,
            dependency: View,
            behavior: BottomSheetBehavior<*>) =
            if (dependency.id == dependencyId) {
                val peek = parent.height - dependency.bottom
                if (peek != behavior.peekHeight) {
                    behavior.peekHeight = peek
                    true
                } else false
            } else false
    }
}