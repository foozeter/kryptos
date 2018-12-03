package com.hayashibambi.kryptos

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.AttrRes
import android.support.annotation.DrawableRes
import android.support.annotation.Px
import android.support.annotation.StyleRes
import android.support.design.widget.TabItem
import android.support.v4.math.MathUtils
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


class StackTabs(context: Context,
       attrs: AttributeSet?,
       @AttrRes defStyleAttr: Int,
       @StyleRes defStyleRes: Int)
    : HorizontalScrollView(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        const val NOT_SELECTED = -1
    }

    val tabCount; get() = tabs.size

    val recentSelectedPosition
        get() = when (state) {
            is Idle -> (state as Idle).position
            is Settling -> (state as Settling).nearest
            is Sliding -> (state as Sliding).origin
            else -> throw IllegalStateException()
        }

    val selectedPosition
        get() = when (state) {
            is Idle -> (state as Idle).position
            is Settling -> (state as Settling).nearest
            is Sliding -> NOT_SELECTED
            else -> throw IllegalStateException()
        }

    var state: State = Idle(0)
        private set(new) {
            val old = field
            field = new
            onStateChanged(old, new)
        }

    @StyleRes
    private val tabTitleStyle: Int

    @StyleRes
    private val tabIconStyle: Int

    @DrawableRes
    private val tabBackground: Int

    @Px
    private val preferredTabHeight: Int

    private val startOffset: Int

    @Px
    private val endOffset: Int

    private val transitionDuration: Long

    private val tabs = mutableListOf<InnerTab>()

    private val strip = TabStrip()

    private val mode: Mode = Scrollable()

    private val tat: ThreeActT

    constructor(context: Context,
                attrs: AttributeSet?,
                @AttrRes defStyleAttr: Int)
            : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context,
                attrs: AttributeSet?): this(context, attrs, 0, 0)

    constructor(context: Context): this(context, null, 0, 0)

    init {
        setBackgroundColor(Color.GREEN)
        // Disable the Scroll Bar
        isHorizontalScrollBarEnabled = false
        // Set us to fill the View port
        isFillViewport = true
        // Add the TabStrip
        addView(strip, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)

        val tabTextFadeDuration: Long
        val tabsSlideDuration: Long
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.TabBar, 0, 0)

        try {
            tabTitleStyle = a.getResourceId(
                R.styleable.TabBar_tabTextStyle,
                R.style.TabTextAppearance)

            tabIconStyle = a.getResourceId(
                R.styleable.TabBar_tabIconStyle,
                R.style.TabIcon)

            tabBackground = a.getResourceId(
                R.styleable.TabBar_tabBackground,
                android.R.attr.selectableItemBackgroundBorderless)

            startOffset = a.getDimensionPixelSize(
                R.styleable.TabBar_startOffset,
                resources.getDimensionPixelSize(R.dimen.tabbar_default_start_offset))

            endOffset = a.getDimensionPixelSize(
                R.styleable.TabBar_endOffset,
                resources.getDimensionPixelSize(R.dimen.tabbar_default_end_offset))

            tabTextFadeDuration = a.getInt(
                R.styleable.TabBar_tabTextFadeDuration,
                resources.getInteger(R.integer.tabbar_default_text_fade_duration)).toLong()

            tabsSlideDuration = a.getInt(
                R.styleable.TabBar_tabsSlideDuration,
                resources.getInteger(R.integer.tabbar_default_slide_duration)).toLong()

        } finally {
            a.recycle()
        }

        transitionDuration = tabsSlideDuration + tabTextFadeDuration * 2
        preferredTabHeight = obtainPreferredTabHeight(attrs)

        tat = ThreeActT(
            tabTextFadeDuration.toFloat(),
            tabsSlideDuration.toFloat(),
            tabTextFadeDuration.toFloat())
    }

    private fun onStateChanged(old: State, new: State) {
        Log.d("mylog", "state was changed! $old ---> $new")
    }

    private fun obtainPreferredTabHeight(attrs: AttributeSet?): Int {
        if (attrs != null) {
            for (i in 0 until attrs.attributeCount) {
                if (attrs.getAttributeName(i) == "layout_height")
                    return attrs.getAttributeIntValue(i, -1)
            }
        }
        return resources.getDimensionPixelSize(
            R.dimen.tabbar_default_height)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        // If we have a MeasureSpec which allows us to decide our height,
        // try and use the default height
//        val idealHeight = dpToPx(DEFAULT_HEIGHT)
        val idealHeight = resources.getDimensionPixelSize(R.dimen.tabbar_default_height)

        when (MeasureSpec.getMode(heightMeasureSpec)) {

            MeasureSpec.AT_MOST ->
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                    Math.min(MeasureSpec.getSize(heightMeasureSpec), idealHeight),
                    MeasureSpec.EXACTLY))

            MeasureSpec.UNSPECIFIED ->
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                    idealHeight, MeasureSpec.EXACTLY))

            MeasureSpec.EXACTLY ->
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

        if (mode is Fixed && childCount == 1) {
            // If we're in fixed mode then we need to make the tab strip
            // is the same width as us so we don't scroll
            val child = getChildAt(0)
            val width = measuredWidth
            if (child.measuredWidth > width) {
                // If the child is wider than us, re-measure it
                // with a widthSpec set to exact our measure width
                val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(
                    heightMeasureSpec,
                    paddingTop + paddingBottom, child.layoutParams.height)
                val childWidthMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int,
                          top: Int, right: Int, bottom: Int) {

        strip.layout(0, 0, strip.measuredWidth, strip.measuredHeight)
        setSelectedPosition(0)
    }

    fun addTab(tab: Tab) {
        val t = newTab(tab)
        tabs.add(t)
        strip.addView(t.view)
    }

    private fun newTab(tab: Tab): InnerTab {
        val t = InnerTab(tab.title, tab.icon, tabs.size, TabView(), false)
        t.view.tag = t
        t.view.setOnClickListener { onTabViewClicked((it.tag as InnerTab)) }
        if (t.icon != null) t.view.icon.setImageDrawable(t.icon)
        if (t.title != null) t.view.title.text = t.title
        return t
    }

    private fun onTabViewClicked(tab: InnerTab) {
        Log.d("mylog", "tab_view(${tab.position}) was clicked!")
        if (selectedPosition != tab.position)
            beginTabsTransition(tab.position)
    }

    private fun beginTabsTransition(destination: Int) {
        ObjectAnimator.ofFloat(0f, 1f).apply {
            duration = 500
            addUpdateListener {
                setTransitionPosition(destination, it.animatedValue as Float)
            }
        }.start()
    }

    private fun onTabSelected(position: Int) {
        Log.d("mylog", "tab($position) was selected!")
    }

    private fun onTabUnSelected(position: Int) {
        Log.d("mylog", "tab($position) was unselected...")
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
            is TabStrip -> super.addView(child, index, params)
            is TabItem -> addTabFromItemView(child)
            else -> throw IllegalArgumentException(
                "Only TabItem instances can be added to TabLayout")
        }
    }

    private fun addTabFromItemView(tabItem: TabItem) {
        addTab(Tab(tabItem.text.toString(), tabItem.icon))
        removeView(tabItem)
    }

    private fun makeTabViewsSlidingPosition(origin: Int, destination: Int, progress: Float) {
        tabs.forEach {

            val x = if (origin == destination) when (it.position) {

                0 -> startOffset

                destination + 1 -> tabs[destination].view.right

                else -> {
                    val p = tabs[it.position - 1].view
                    p.left + p.icon.width
                }

            } else when (it.position) {

                0 -> startOffset

                origin + 1 -> {
                    val f = tabs[origin].view
                    (f.right - f.title.width * progress).toInt()
                }

                destination + 1 -> {
                    val t = tabs[destination].view
                    (t.left + t.icon.width + t.title.width * progress).toInt()
                }

                else -> {
                    val p = tabs[it.position - 1].view
                    p.left + p.icon.width
                }
            }

            ViewCompat.offsetLeftAndRight(it.view, x - it.view.left)
        }
    }

    private fun makeTabViewsIdlePosition(position: Int) {
        makeTabViewsSlidingPosition(position, position, 0f)
    }

    private fun makeTabViewActivatingPosition(position: Int, progress: Float) {
        tabs[position].view.title.alpha = progress
    }

    private fun makeTabViewInactivatingPosition(position: Int, progress: Float) {
        tabs[position].view.title.alpha = 1f - progress
    }

    private fun makeAllTabViewsInactivated() {
        tabs.forEach { it.view.title.alpha = 0f }
    }

    private fun makeAllTabViewsInActivatedExcept(position: Int) {
        tabs.forEach {
            if (it.position != position)
                it.view.title.alpha = 0f
        }
    }

    private fun makeTabViewActivated(position: Int) {
        tabs[position].view.title.alpha = 1f
    }

    fun setSelectedPosition(position: Int) {
        setTransitionPosition(position, 1f)
    }

    fun setTransitionPosition(destination: Int, progress: Float) {
        val origin = recentSelectedPosition
        tat.t = progress

        when {

            origin == destination
                    || progress == 1f -> {

                makeTabViewsIdlePosition(destination)
                makeAllTabViewsInActivatedExcept(destination)
                makeTabViewActivated(destination)
                setStateAsIdle(destination)
            }

            progress == 0f -> {
                makeTabViewsIdlePosition(origin)
                makeAllTabViewsInActivatedExcept(origin)
                makeTabViewActivated(origin)
                setStateAsIdle(origin)
            }

            tat.act == ThreeActT.Act.ONE -> {
                makeTabViewsIdlePosition(origin)
                makeAllTabViewsInActivatedExcept(origin)
                makeTabViewInactivatingPosition(origin, tat.topicalT)
                setStateAsSettling(origin, destination, origin)
            }

            tat.act == ThreeActT.Act.TWO -> {
                makeTabViewsSlidingPosition(origin, destination, tat.topicalT)
                makeAllTabViewsInactivated()
                setStateAsSliding(origin, destination)
            }

            tat.act == ThreeActT.Act.THREE -> {
                makeTabViewsIdlePosition(destination)
                makeAllTabViewsInActivatedExcept(destination)
                makeTabViewActivatingPosition(destination, tat.topicalT)
                setStateAsSettling(origin, destination, destination)
            }
        }
    }

    private fun setStateAsIdle(position: Int) {
        val sp = selectedPosition
        val anyTabIsSelected = sp != NOT_SELECTED
        val isNotSamePosition = sp != position

        if (isNotSamePosition || state !is Idle) {
            state = Idle(position)
            if (anyTabIsSelected && isNotSamePosition) onTabUnSelected(sp)
            if (isNotSamePosition) onTabSelected(selectedPosition)
        }
    }

    private fun setStateAsSettling(
        origin: Int, destination: Int, nearest: Int) {

        val sp = selectedPosition
        val anyTabIsSelected = sp != NOT_SELECTED
        val isNotSamePosition = sp != nearest

        if (isNotSamePosition || state !is Settling) {
            state = Settling(origin, destination, nearest)
            if (anyTabIsSelected && isNotSamePosition) onTabUnSelected(sp)
            if (isNotSamePosition) onTabSelected(selectedPosition)
        }
    }

    private fun setStateAsSliding(origin: Int, destination: Int) {
        val s = state
        if (s !is Sliding || s.origin != origin || s.destination != destination) {
            val sp = selectedPosition
            state = Sliding(origin, destination)
            if (sp != NOT_SELECTED) onTabUnSelected(sp)
        }
    }

    inner class TabView : LinearLayout(
        this@StackTabs.context, null, 0, R.style.Tab) {

        val icon = ImageView(
            context, null, 0, this@StackTabs.tabIconStyle).apply {
            layoutParams = LinearLayout.LayoutParams(
                this@StackTabs.preferredTabHeight,
                this@StackTabs.preferredTabHeight)
        }

        val title = TextView(
            context, null, 0, this@StackTabs.tabTitleStyle).apply {
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                this@StackTabs.preferredTabHeight)
        }

        init {
            addView(icon)
            addView(title)
//            setBackgroundResource(this@StackTabs.tabBackground)
        }
    }

    private class InnerTab(
        val title: String?,
        val icon: Drawable?,
        val position: Int,
        val view: TabView,
        var isSelected: Boolean) {
    }

    data class Tab(
        var title: String?,
        var icon: Drawable?) {

    }

    private interface Mode
    private class Scrollable: Mode
    private class Fixed(val tabWidth: Int): Mode

    private inner class TabStrip: ViewGroup(this@StackTabs.context) {

        init {
            setBackgroundColor(Color.RED)
        }

        override fun onLayout(changed: Boolean, left: Int,
                              top: Int, right: Int, bottom: Int) {
            tabs.forEach {
                it.view.layout(0, 0,
                    it.view.measuredWidth, it.view.measuredHeight)
            }
        }

        @SuppressWarnings("SwitchIntDef")
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
                throw IllegalArgumentException(
                    "layout_height of the TabStrip is not specified.")
            } else if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
                // HorizontalScrollView will first measure use with UNSPECIFIED, and then with EXACTLY.
                // Ignore the second call since anything we do will be overwritten anyway
                setMeasuredDimension(measuredWidth, measuredHeight)
                return
            }

            val height = MeasureSpec.getSize(heightMeasureSpec)
            val width: Int

            when (this@StackTabs.mode) {
                is Fixed -> TODO("implement")

                is Scrollable -> {

                    var largestTabWidth = 0
                    tabs.forEach {
                        val view = it.view
                        view.icon.layoutParams.width = height
                        view.icon.layoutParams.height = height
                        view.title.layoutParams.width = LayoutParams.WRAP_CONTENT
                        view.title.layoutParams.height = height
                        view.measure(
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                        )

                        largestTabWidth = Math.max(largestTabWidth, view.measuredWidth)
                    }

                    val idealWidth = this@StackTabs.startOffset + this@StackTabs.endOffset +
                                largestTabWidth + (this@StackTabs.tabCount - 1) * height

                    width = when (MeasureSpec.getMode(widthMeasureSpec)) {
                        MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
                        MeasureSpec.AT_MOST -> Math.min(MeasureSpec.getSize(widthMeasureSpec), idealWidth)
                        MeasureSpec.UNSPECIFIED -> idealWidth
                        else -> throw IllegalArgumentException()
                    }
                }

                else -> throw IllegalArgumentException()
            }

            setMeasuredDimension(width, height)
        }
    }

    private class ThreeActT(
        weightAct1: Float,
        weightAct2: Float,
        weightAct3: Float) {

        var act = Act.ONE; private set

        var topicalT = 0f; private set

        private var thresholdAct1 = 0f

        private var thresholdAct2 = 0f

        var t = 0f
            set(value) {
                field = MathUtils.clamp(value, 0f, 1f)
                when {

                    field < thresholdAct1 -> {
                        act = Act.ONE
                        topicalT = field / thresholdAct1
                    }

                    field < thresholdAct2 -> {
                        act = Act.TWO
                        topicalT = (field - thresholdAct1) / (thresholdAct2 - thresholdAct1)
                    }

                    else -> {
                        act = Act.THREE
                        topicalT = (field - thresholdAct2) / (1f - thresholdAct2)
                    }
                }
            }

        init {
            setWeights(weightAct1, weightAct2, weightAct3)
        }

        fun setWeights(act1: Float, act2: Float, act3: Float) {
            val weightSum = act1 + act2 + act3
            thresholdAct1 = act1 / weightSum
            thresholdAct2 = (act1 + act2) / weightSum
        }

        enum class Act {
            ONE, TWO, THREE
        }
    }

    interface State
    data class Sliding(val origin: Int, val destination: Int): State
    data class Settling(val origin: Int, val destination: Int, val nearest: Int): State
    data class Idle(val position: Int): State
}
