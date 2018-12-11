package com.hayashibambi.kryptos

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class ShrinkFrameLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val amountOfWidth: Int
    private val amountOfHeight: Int

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ShrinkFrameLayout, 0, 0)
        amountOfWidth = a.getDimensionPixelSize(R.styleable.ShrinkFrameLayout_shrink_amountOfWidth, 0)
        amountOfHeight = a.getDimensionPixelSize(R.styleable.ShrinkFrameLayout_shrink_amountOfHeight, 0)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSpec = widthMeasureSpec
        var heightSpec = heightMeasureSpec
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            widthSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec) - amountOfWidth,
                MeasureSpec.EXACTLY)
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            heightSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec) - amountOfHeight,
                MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthSpec, heightSpec)
    }
}