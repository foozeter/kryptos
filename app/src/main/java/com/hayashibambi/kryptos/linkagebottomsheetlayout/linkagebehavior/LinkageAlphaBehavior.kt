package com.hayashibambi.kryptos.linkagebottomsheetlayout.linkagebehavior

import android.content.Context
import android.util.AttributeSet
import android.view.View

class LinkageAlphaBehavior<V: View>(
    context: Context, attrs: AttributeSet)
    : LinkageValueBehavior<V>(context, attrs) {

    override fun onApplyValue(target: View, value: Float) {
        target.alpha = value
    }
}