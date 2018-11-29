package com.hayashibambi.kryptos

 import android.content.Context
 import android.support.annotation.AttrRes
 import android.support.annotation.StyleRes
 import android.util.AttributeSet
 import android.widget.LinearLayout

class TabBar(context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int): LinearLayout(context, attrs, defStyleAttr) {

     constructor(context: Context,
                 attrs: AttributeSet?): this(context, attrs, 0)

     constructor(context: Context): this(context, null, 0)

     init {
         loadAttributes(context, attrs, defStyleAttr)
     }

     private fun loadAttributes(
             context: Context,
             attrs: AttributeSet?,
             @AttrRes defStyleAttr: Int) {}
 }
