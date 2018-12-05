package com.hayashibambi.kryptos

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import com.google.android.material.tabs.TabItem
import androidx.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class KryptosTabLayout(context: Context,
       attrs: AttributeSet?,
       @AttrRes defStyleAttr: Int,
       @StyleRes defStyleRes: Int): FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val dimens = Dimens(context)

    private val tabs = mutableListOf<Tab>()

    private val centerGravity: Boolean

    private var selectedPosition = 0

    constructor(context: Context,
                attrs: AttributeSet?,
                @AttrRes defStyleAttr: Int): this(context, attrs, defStyleAttr, 0)

    constructor(context: Context,
                attrs: AttributeSet?): this(context, attrs, 0, 0)

    constructor(context: Context): this(context, null, 0, 0)

    init {
        centerGravity = true
//        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.KryptosTabLayout, 0, 0)
//        a.recycle()
    }

    private fun loadAttributes(
            context: Context,
            attrs: AttributeSet?,
            @AttrRes defStyleAttr: Int,
            @StyleRes defStyleRes: Int) {}

    fun select(position: Int) {
        if (selectedPosition != position)
            TransitionManager.beginDelayedTransition(this)
        selectWithoutAnimation(position)
    }

    fun selectWithoutAnimation(position: Int) {
        val old = selectedPosition
        selectedPosition = position
        tabs[old].view.setAsUnselected()
        tabs[position].view.setAsSelected()
        if (old == position) onTabReSelected(tabs[position])
        else onTabSelected(tabs[position])
    }

    private fun onTabSelected(tab: Tab) {
        Log.d("mylog", "selected(${tab.position})")
    }

    private fun onTabReSelected(tab: Tab) {
        Log.d("mylog", "re-selected(${tab.position})")
    }

    private fun onTabClick(tab: Tab) {
        Log.d("mylog", "tab click(${tab.position})")
        select(tab.position)
    }

    override fun addView(child: View) {
        addView(child, childCount, LayoutParams(0, 0))
    }

    override fun addView(child: View, index: Int) {
        addView(child, index, LayoutParams(0, 0))
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        addView(child, childCount, params)
    }

    override fun addView(child: View, width: Int, height: Int) {
        addView(child, childCount, LayoutParams(width, height))
    }

    override fun addView(child: View, index: Int,
                         params: ViewGroup.LayoutParams) {
        when (child) {
            is TabView -> super.addView(child, index, params)
            is TabItem -> addTab(child)
            else -> throw IllegalArgumentException(
                "Only TabItem or TabView instances can be added to the StackTabs")
        }
    }

    private fun addTab(tabItem: TabItem) {
        val view = TabView(context)
        val tab = Tab(tabItem.text, tabItem.icon, tabs.size, view)

        view.apply {
            tag = tab
            text.text = tab.text
            icon.setImageDrawable(tab.icon)
            icon.setOnClickListener { onTabClick(tag as Tab) }
            if (tab.isSelected) setAsSelected() else setAsUnselected()
        }

        tabs.add(tab)
        addView(tab.view)
        removeView(tabItem)
    }

    private fun exactly(size: Int)
            = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)

    private fun atMost(size: Int)
            = MeasureSpec.makeMeasureSpec(size, MeasureSpec.AT_MOST)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, exactly(dimens.tabHeight))

        val tabWidth = when (tabs.size) {
            0 -> 0
            1 -> dimens.maxTabWidth
            else -> Math.min(
                dimens.maxTabWidth,
                measuredWidth - (tabs.size - 1) * dimens.iconWidth)
        }

        tabs.forEach {
            it.view.text.layoutParams.width = tabWidth - dimens.iconWidth
            it.view.text.layoutParams.height = dimens.tabHeight
            it.view.icon.layoutParams.width = dimens.iconWidth
            it.view.icon.layoutParams.height = dimens.tabHeight
            it.view.measure(atMost(tabWidth), exactly(measuredHeight))
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var startOffset = 0
        if (centerGravity) {
            var widthSum = 0
            tabs.forEach { widthSum += it.view.measuredWidth }
            startOffset += (right - left - widthSum) / 2
        }

        tabs.forEach {
            it.view.layout(startOffset, 0, startOffset + it.view.measuredWidth, it.view.measuredHeight)
            startOffset += it.view.width
        }
    }

    private inner class Tab(
        val text: CharSequence?,
        val icon: Drawable?,
        val position: Int,
        val view: TabView) {

        val isSelected; get() =
            position == this@KryptosTabLayout.selectedPosition
    }

    private inner class TabView(context: Context): LinearLayout(context) {

        val icon = ImageView(context, null, 0, R.style.KryptosTabLayoutTabIcon)
        val text = TextView(context, null, 0, R.style.KryptosTabLayoutText)

        init {
            icon.layoutParams = LayoutParams(0, 0)
            text.layoutParams = LayoutParams(0, 0)
            addView(icon)
            addView(text)

            orientation = LinearLayout.HORIZONTAL
        }

        fun setAsSelected() {
            text.visibility = View.VISIBLE
        }

        fun setAsUnselected() {
            text.visibility = View.GONE
        }
    }

    private class Dimens(context: Context) {

        companion object {
            private const val TAB_HEIGHT = 56
            private const val MAX_TEXT_WIDTH = 98
            private const val ICON_WIDTH = TAB_HEIGHT
            private const val MAX_TAB_WIDTH = ICON_WIDTH + MAX_TEXT_WIDTH
            private const val ICON_PADDING = 16
        }

        private val density = context.resources.displayMetrics.density

        val tabHeight; get() = (TAB_HEIGHT * density).toInt()
        val maxTabWidth; get() = (MAX_TAB_WIDTH * density).toInt()
        val maxTextWidth; get() = (MAX_TEXT_WIDTH * density).toInt()
        val iconWidth; get() = (ICON_WIDTH * density).toInt()
        val iconPadding; get() = (ICON_PADDING * density).toInt()
    }
}
