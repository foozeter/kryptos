package com.hayashibambi.kryptos

 import android.animation.ObjectAnimator
 import android.content.Context
 import android.graphics.Point
 import android.graphics.drawable.Drawable
 import android.support.annotation.AttrRes
 import android.support.v4.math.MathUtils
 import android.support.v4.view.ViewCompat
 import android.util.AttributeSet
 import android.util.Log
 import android.view.View
 import android.widget.FrameLayout
 import android.widget.ImageView
 import android.widget.LinearLayout
 import android.widget.TextView

class TabBar(context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int): FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val INVALID_POSITION = -1
        private const val tabIconSize = 200
    }

    constructor(context: Context,
                 attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context): this(context, null, 0)

    val tabCount; get() = tabs.size

    val latestPosition; get() = when (state) {
        is Idle -> (state as Idle).position
        else -> (state as Sliding).from
    }

    private val tabs = mutableMapOf<Int, TabHolder>()

    private var state: State = Idle(INVALID_POSITION)
        set(value) {
            when {
                value == field -> if(value is Idle && field is Idle) selectTab(value.position) else return
                value is Sliding && field is Idle -> unSelectTab((field as Idle).position)
                value is Idle -> {
                    val prev = if (field is Idle) (field as Idle).position else (field as Sliding).from
                    val now = value.position
                    if (prev != now) unSelectTab(prev)
                    selectTab(now)
                }
            }

            field = value
        }

    /**
     * This should be called only by the setter of 'state' property.
     */
    private fun selectTab(position: Int) {
        val tab = tabs[position]
        tab ?: return
        if (tab.isSelected)
            onTabReSelected(tab)
        else {
            tab.isSelected = true
            onTabSelected(tab)
        }
    }

    /**
     * This should be called only by the setter of 'state'.
     */
    private fun unSelectTab(position: Int) {
        val tab = tabs[position]
        if (tab != null && tab.isSelected) {
            tab.isSelected = false
            onTabUnSelected(tab)
        }
    }

    private fun onTabSelected(tab: TabHolder) {
        Log.d("mylog", "onTabSelected(${tab.position}")
    }

    private fun onTabReSelected(tab: TabHolder) {
        Log.d("mylog", "onTabReSelected(${tab.position}")
    }

    private fun onTabUnSelected(tab: TabHolder) {
        Log.d("mylog", "onTabUnSelected(${tab.position}")
    }

     init {
         loadAttributes(context, attrs, defStyleAttr)
         state = Idle(position = 1)
     }

    fun addTab(title: String, icon: Drawable) {
        val tab = TabHolder(title, icon, tabs.size)
        val view = TabView.make(context, tabIconSize)
        view.tag = tab
        view.setOnClickListener { onTabClick(it.tag as TabHolder) }
        tab.view = view
        tab.isSelected = state is Idle && (state as Idle).position == tab.position
        tabs[tab.position] = tab
        addView(tab.view)
    }

    private var weightCollapseTab = 2
    private var weightScrollTabs = 6
    private var weightExpandTab = 2
    private var sum = weightCollapseTab + weightScrollTabs + weightExpandTab
    fun setTransitionPosition(toPosition: Int, positionOffset: Float) {
        val fromPosition = latestPosition
        val offset = MathUtils.clamp(positionOffset, 0f, 1f)
        if (offset <= weightCollapseTab / sum) {
            Log.d("mylog", "collapsing tab part ($offset)")
            getTabViewAt(fromPosition).title.alpha = offset * sum / weightCollapseTab
        } else if (offset <= (weightCollapseTab + weightScrollTabs) / sum) {
            Log.d("mylog", "scrolling tabs part ($offset)")
            val t = (offset * sum - weightCollapseTab) / weightScrollTabs
            setScrollPosition(toPosition, t)
        } else {
            Log.d("mylog", "expanding tab part ($offset)")
            getTabViewAt(toPosition).title.alpha =
                    (offset * sum - (weightCollapseTab + weightScrollTabs)) / weightExpandTab
        }
    }

    private fun setScrollPosition(toPosition: Int, positionOffset: Float) {
        val fromPosition = latestPosition
        if (toPosition != fromPosition) {
            val offset = MathUtils.clamp(positionOffset, 0f, 1f)
            val from = getTabViewAt(fromPosition)
            val to = getTabViewAt(toPosition)

            val d: Int; val s: Int; val e: Int
            if (fromPosition < toPosition) {
                d = (getTabViewAt(fromPosition + 1).left - (from.right - from.title.width * offset)).toInt()
                s = fromPosition + 1
                e = s + toPosition - fromPosition - 1
            } else {
                d = (getTabViewAt(toPosition + 1).left - (to.right + from.title.width * (offset - 1f))).toInt()
                s = toPosition + 1
                e = s + fromPosition - toPosition - 1
            }

            for (i in s..e) {
                ViewCompat.offsetLeftAndRight(getTabViewAt(i), -d)
            }

            when (offset) {
                0f -> if (state is Sliding) state = Idle(position = fromPosition)
                1f -> if (state is Sliding) state = Idle(position = toPosition)
                else -> if (state is Idle) state = Sliding(from = fromPosition, to = toPosition)
            }

        } else {
            state = Idle(position = toPosition)
        }
    }

    fun setPosition(position: Int) = setTransitionPosition(position, 1f)

    private fun onTabClick(tab: TabHolder) {
        Log.d("mylog", "tab(${tab.position} clicked!")
        if (state is Idle && (state as Idle).position == tab.position) {
            // this will cause an 'OnTabReSelected' event.
            setPosition(tab.position)
        } else {
            val anim = ObjectAnimator.ofFloat(0f, 1f)
            anim.duration = 400
            anim.addUpdateListener { setTransitionPosition(tab.position, it.animatedValue as Float) }
            anim.start()
        }
    }

    private val sizeSpec = Point()
    private val modeSpec = Point()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        sizeSpec.set(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        modeSpec.set(MeasureSpec.getMode(widthMeasureSpec), MeasureSpec.getMode(heightMeasureSpec))

        // todo; refactor
        setMeasuredDimension(sizeSpec.x, tabIconSize)

        val tabTitleWidth = measuredWidth - tabCount * tabIconSize
        val tabWidth = tabIconSize + tabTitleWidth
        val tabHeight = tabIconSize

        tabs.forEach {
            val tab = it.value.view!!
            tab.measure(MeasureSpec.makeMeasureSpec(tabWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(tabHeight, MeasureSpec.EXACTLY))
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        tabs.forEach {
            val pos = it.key
            val tab = it.value.view!!
            when (pos) {
                0 -> {
                    tab.layout(0, 0, tab.measuredWidth, tab.measuredHeight)
                }

                latestPosition + 1 -> {
                    val prev = getChildAt(pos - 1)
                    tab.layout(prev.right, 0, prev.right + tab.measuredWidth, tab.measuredHeight)
                }

                else -> {
                    val prev = getChildAt(pos - 1) as TabView
                    val l = prev.left + prev.icon.width
                    tab.layout(l, 0, l + tab.measuredWidth, tab.measuredHeight)
                }
            }
        }
    }

    fun getTabAt(position: Int) = tabs[position]!!.publicTab

    private fun getTabViewAt(position: Int) = tabs[position]!!.view!!

    private fun loadAttributes(
             context: Context,
             attrs: AttributeSet?,
             @AttrRes defStyleAttr: Int) {}

    private class TabView private constructor(
        context: Context, height: Int): LinearLayout(context) {

        companion object {
            fun make(context: Context, height: Int): TabView {
                val tv = TabView(context, height)
                tv.layoutParams = FrameLayout.LayoutParams(0, height)
                return tv
            }
        }

        val icon = ImageView(context)
        val title = TextView(context)

        init {
            val lpi = LinearLayout.LayoutParams(height, height)
            val lpt = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height)
            icon.layoutParams = lpi
            title.layoutParams = lpt
            addView(icon)
            addView(title)

            // todo; delete
            icon.setImageResource(R.mipmap.ic_launcher_round)
            title.text = "collections"
            //

            orientation = LinearLayout.HORIZONTAL
            isClickable = true
            isFocusable = true
        }
    }

    private class TabHolder(val title: String, val icon: Drawable, val position: Int) {

        val publicTab = object: Tab {
            override val position; get() = this@TabHolder.position
            override val isSelected; get() = this@TabHolder.isSelected
            override val title; get() = this@TabHolder.title
            override val icon; get() = this@TabHolder.icon
        }

        var view: TabView? = null
            set(value) {
                field = value
                invalidateTabSelectionState()
            }

        var isSelected = false
            set(value) {
                if (field != value) {
                    field = value
                    invalidateTabSelectionState()
                }
            }

        private fun invalidateTabSelectionState() {
            view?.title?.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
        }
    }

    interface Tab {
        val position: Int
        val isSelected: Boolean
        val title: String
        val icon: Drawable
    }

    private interface State
    private data class Sliding(val from: Int, val to: Int): State
    private data class Idle(val position: Int): State

    private fun Float.isEither(a: Float, b: Float) = this == a || this == b
    private fun Float.isBetween(min: Float, max: Float) = min < this && this < max
 }
