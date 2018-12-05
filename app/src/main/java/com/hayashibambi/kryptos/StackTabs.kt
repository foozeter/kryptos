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
import android.view.animation.AccelerateDecelerateInterpolator
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

        const val UNSPECIFIED = -2

        private const val DEFAULT_TAB_HEIGHT = 56 // dip

        private const val DEFAULT_TAB_ICON_WIDTH = DEFAULT_TAB_HEIGHT

        private const val DEFAULT_TAB_TEXT_WIDTH = UNSPECIFIED

        private const val DEFAULT_TAB_TEXT_MIN_WIDTH = UNSPECIFIED

        private const val DEFAULT_TAB_TEXT_MAX_WIDTH = UNSPECIFIED
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

    var autoScroll = true

    @StyleRes
    private val tabTitleStyle: Int

    @StyleRes
    private val tabIconStyle: Int

    @DrawableRes
    private val tabBackground: Int

    @Px
    private val tabIconWidth: Int

    @Px
    private val tabTextMinWidth: Int

    @Px
    private val tabTextMaxWidth: Int

    @Px
    private val tabTextWidth: Int

    private val startOffset: Int

    @Px
    private val endOffset: Int

    private val transitionDuration: Long

    private val tabs = mutableListOf<Tab>()

    private val strip = TabStrip()

    private var mode: Mode = Fixed(0)

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

        val tabsSettlingDuration: Long
        val tabsSlidingDuration: Long
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.StackTabs, 0, 0)

        try {
            tabTitleStyle = a.getResourceId(
                R.styleable.StackTabs_tabTextStyle,
                R.style.TabTextAppearance)

            tabIconStyle = a.getResourceId(
                R.styleable.StackTabs_tabIconStyle,
                R.style.TabIcon)

            tabBackground = a.getResourceId(
                R.styleable.StackTabs_tabBackground,
                android.R.attr.selectableItemBackgroundBorderless)

            startOffset = a.getDimensionPixelSize(
                R.styleable.StackTabs_startOffset,
                resources.getDimensionPixelSize(R.dimen.tabbar_default_start_offset))

            endOffset = a.getDimensionPixelSize(
                R.styleable.StackTabs_endOffset,
                resources.getDimensionPixelSize(R.dimen.tabbar_default_end_offset))

            tabsSettlingDuration = a.getInt(
                R.styleable.StackTabs_tabTextFadeDuration,
                resources.getInteger(R.integer.tabbar_default_text_fade_duration)).toLong()

            tabsSlidingDuration = a.getInt(
                R.styleable.StackTabs_tabsSlideDuration,
                resources.getInteger(R.integer.tabbar_default_slide_duration)).toLong()

            tabIconWidth = a.getDimensionPixelSize(
                R.styleable.StackTabs_tabIconWidth,
                dpToPx(DEFAULT_TAB_ICON_WIDTH))

            tabTextWidth = a.getDimensionPixelSize(
                R.styleable.StackTabs_tabTextWidth,
                DEFAULT_TAB_TEXT_WIDTH)

            Log.d("mylog", "textwidth=$tabTextWidth")

            tabTextMinWidth = a.getDimensionPixelSize(
                R.styleable.StackTabs_tabTextMinWidth,
                DEFAULT_TAB_TEXT_MIN_WIDTH)

            tabTextMaxWidth = a.getDimensionPixelSize(
                R.styleable.StackTabs_tabTextMaxWidth,
                DEFAULT_TAB_TEXT_MAX_WIDTH)

        } finally {
            a.recycle()
        }

        transitionDuration = tabsSlidingDuration + tabsSettlingDuration * 2

        tat = ThreeActT(
            tabsSettlingDuration.toFloat(),
            tabsSlidingDuration.toFloat(),
            tabsSettlingDuration.toFloat())
    }

    private fun onStateChanged(old: State, new: State) {
        Log.d("mylog", "state was changed! -> $new")
    }

    @Px
    private fun obtainIdealTabHeight(attrs: AttributeSet?): Int  {
        val h = obtainAttributeInt(attrs, "layout_height", -1)
        return if (h < 0) dpToPx(DEFAULT_TAB_HEIGHT) else h
    }

    private fun obtainAttributeValue(attrs: AttributeSet?, name: String): String? {
        if (attrs != null) {
            for (i in 0 until attrs.attributeCount) {
                if (attrs.getAttributeName(i) == name)
                    return attrs.getAttributeValue(i)
            }
        }
        return null
    }

    private fun obtainAttributeInt(attrs: AttributeSet?, name: String, def: Int)
            = obtainAttributeValue(attrs, name)?.toInt() ?: def

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        // If we have a MeasureSpec which allows us to decide our height,
        // try and use the default height
        val idealHeight = dpToPx(DEFAULT_TAB_HEIGHT)

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

    fun addTab(text: String?, icon: Drawable?) {
        val t = newTab(text, icon)
        tabs.add(t)
        strip.addView(t.view)
    }

    private fun newTab(text: String?, icon: Drawable?): Tab {
        val t = Tab(text, icon, tabs.size, TabView())
        t.view.tag = t
        t.view.setOnClickListener { onTabViewClicked((it.tag as Tab)) }
        if (t.icon != null) t.view.icon.setImageDrawable(t.icon)
        if (t.title != null) t.view.text.text = t.title
        return t
    }

    private fun onTabViewClicked(tab: Tab) {
        Log.d("mylog", "tab_view(${tab.position}) was clicked!")
        if (selectedPosition != tab.position) {
            beginTabsTransition(tab.position)
        }
    }

    private fun beginTabsTransition(destination: Int) {
        ObjectAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                setTransitionPosition(destination, it.animatedValue as Float)

            }
        }.start()
    }

    private fun onTabSelected(position: Int) {
        Log.d("mylog", "tab($position) was selected!")
    }

    private fun onTabUnselected(position: Int) {
        Log.d("mylog", "tab($position) was unselected...")
    }

    private fun onTabCompletelySelected(position: Int) {
        Log.d("mylog", "tab($position) was selected completely!!")
    }

    private fun onTabCompletelyUnselected(position: Int) {
        Log.d("mylog", "tab($position) was unselected completely...")
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
                "Only TabItem instances can be added to the StackTabs")
        }
    }

    private fun addTabFromItemView(tabItem: TabItem) {
        addTab(tabItem.text.toString(), tabItem.icon)
        removeView(tabItem)
    }

    private fun applyAutoScroll(target: Int, progress: Float) {
        if (mode is Scrollable) {
            val v = tabs[target].view
            val d = (v.right - v.text.width / 2) - (scrollX + width / 2)
            scrollBy((d * progress).toInt(), 0)
        }
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
                    (f.right - f.text.width * progress).toInt()
                }

                destination + 1 -> {
                    val t = tabs[destination].view
                    (t.left + t.icon.width + t.text.width * progress).toInt()
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
        tabs[position].view.text.alpha = progress
    }

    private fun makeTabViewInactivatingPosition(position: Int, progress: Float) {
        tabs[position].view.text.alpha = 1f - progress
    }

    private fun makeAllTabViewsInactivated() {
        tabs.forEach { it.view.text.alpha = 0f }
    }

    private fun makeAllTabViewsInActivatedExcept(position: Int) {
        tabs.forEach {
            if (it.position != position)
                it.view.text.alpha = 0f
        }
    }

    private fun makeTabViewActivated(position: Int) {
        tabs[position].view.text.alpha = 1f
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
                if (autoScroll) applyAutoScroll(destination, 1f)
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
                if (autoScroll) applyAutoScroll(destination, tat.topicalT)
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
            if (anyTabIsSelected && isNotSamePosition) onTabCompletelyUnselected(sp)
            onTabCompletelySelected(selectedPosition)
        }
    }

    private fun setStateAsSettling(
        origin: Int, destination: Int, nearest: Int) {

        val sp = selectedPosition
        val anyTabIsSelected = sp != NOT_SELECTED
        val isNotSamePosition = sp != nearest

        if (isNotSamePosition || state !is Settling) {
            val oldState = state
            state = Settling(origin, destination, nearest)
            if (anyTabIsSelected) {
                if (oldState !is Idle || isNotSamePosition) onTabCompletelyUnselected(sp)
                else onTabUnselected(sp)
            }

            if (isNotSamePosition) onTabSelected(selectedPosition)
        }
    }

    private fun setStateAsSliding(origin: Int, destination: Int) {
        val s = state
        if (s !is Sliding || s.origin != origin || s.destination != destination) {
            val sp = selectedPosition
            state = Sliding(origin, destination)
            if (sp != NOT_SELECTED) onTabCompletelyUnselected(sp)
        }
    }

    private fun dpToPx(dp: Int) =
        (context.resources.displayMetrics.density * dp).toInt()

    inner class TabView : LinearLayout(
        this@StackTabs.context, null, 0, R.style.Tab) {

        val idealTabHeight = dpToPx(DEFAULT_TAB_HEIGHT)
        val icon = ImageView(
            context, null, 0, this@StackTabs.tabIconStyle).apply {
            layoutParams = LinearLayout.LayoutParams(idealTabHeight, idealTabHeight)
        }

        val text = TextView(
            context, null, 0, this@StackTabs.tabTitleStyle).apply {
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                idealTabHeight)
        }

        init {
            addView(icon)
            addView(text)
//            setBackgroundResource(this@StackTabs.tabBackground)
        }
    }

    inner class Tab(
        val title: String?,
        val icon: Drawable?,
        val position: Int,
        val view: TabView) {

        val isSelected; get() =
            position == this@StackTabs.selectedPosition
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

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
                throw IllegalArgumentException(
                    "layout_height of the TabStrip is not specified.")

            } else when (this@StackTabs.mode) {
                is Fixed -> onMeasureInFixedMode(
                    MeasureSpec.getSize(widthMeasureSpec),
                    MeasureSpec.getSize(heightMeasureSpec),
                    MeasureSpec.getMode(widthMeasureSpec),
                    MeasureSpec.getMode(heightMeasureSpec))

                is Scrollable -> onMeasureInScrollableMode(
                    MeasureSpec.getSize(widthMeasureSpec),
                    MeasureSpec.getSize(heightMeasureSpec),
                    MeasureSpec.getMode(widthMeasureSpec))
            }
        }

        private fun onMeasureInFixedMode(widthSize:Int, heightSize: Int, widthMode: Int, heightMode: Int) {
//            Log.d("mylog", "w=${widthSize}, " +
//                    "exactly?=${widthMode==MeasureSpec.EXACTLY}, " +
//                    "atmost?=${widthMode==MeasureSpec.AT_MOST}, " +
//                    "unspecified?=${widthMode==MeasureSpec.UNSPECIFIED}")

            if (widthMode != MeasureSpec.EXACTLY) {
//                 HorizontalScrollView will first measure use with UNSPECIFIED, and then with EXACTLY.
//                 Ignore the first call since anything we do will be overwritten anyway
                setMeasuredDimension(0, 0)
                return
            }

            val tabWidth =
                if (this@StackTabs.tabCount == 0) 0
                else widthSize - heightSize * (this@StackTabs.tabCount - 1)

            if (tabWidth <= heightSize) {
                this@StackTabs.mode = Scrollable()
                measure(MeasureSpec.makeMeasureSpec(widthSize, widthMode),
                    MeasureSpec.makeMeasureSpec(heightSize, heightMode))
                return
            }

            setMeasuredDimension(widthSize, heightSize)

            this@StackTabs.tabs.forEach {
                val view = it.view
                view.icon.layoutParams.width = heightSize
                view.icon.layoutParams.height = heightSize
                view.text.layoutParams.width = tabWidth - heightSize
                view.text.layoutParams.height = heightSize
                view.measure(
                    MeasureSpec.makeMeasureSpec(tabWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
                )
            }
        }

        @SuppressWarnings("SwitchIntDef")
        private fun onMeasureInScrollableMode(widthSize:Int, heightSize: Int, widthMode: Int) {
            if (widthMode == MeasureSpec.EXACTLY) {
//                 HorizontalScrollView will first measure use with UNSPECIFIED, and then with EXACTLY.
//                 Ignore the second call since anything we do will be overwritten anyway
                setMeasuredDimension(measuredWidth, measuredHeight)
                return
            }

            var largestTabWidth = 0
            this@StackTabs.tabs.forEach {
                val view = it.view
                view.icon.layoutParams.width = tabIconWidth
                view.icon.layoutParams.height = heightSize
                view.text.layoutParams.height = heightSize
                if (tabTextMinWidth != UNSPECIFIED) view.text.minWidth = tabTextMinWidth
                if (tabTextMaxWidth != UNSPECIFIED) view.text.maxWidth = tabTextMaxWidth

                if (tabTextWidth == UNSPECIFIED) {
                    Log.d("mylog", "1111")
                    view.text.layoutParams.width = LayoutParams.WRAP_CONTENT
                    view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY))

                } else {
                    Log.d("mylog", "22222")
                    view.text.layoutParams.width = tabTextWidth
                    view.measure(MeasureSpec.makeMeasureSpec(tabIconWidth + tabTextWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY))
                }

                largestTabWidth = Math.max(largestTabWidth, view.measuredWidth)
            }

            val idealWidth = this@StackTabs.startOffset + this@StackTabs.endOffset +
                    largestTabWidth + (this@StackTabs.tabCount - 1) * heightSize

            val width = when (widthMode) {
                MeasureSpec.AT_MOST -> Math.min(widthSize, idealWidth)
                MeasureSpec.UNSPECIFIED -> idealWidth
                else -> throw IllegalArgumentException()
            }

            setMeasuredDimension(width, heightSize)
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
