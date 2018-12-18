package com.hayashibambi.kryptos.linkagebottomsheetlayout.linkagebehavior

import android.content.Context
import android.util.AttributeSet
import android.view.View

class LinkagePropertyBehavior<V: View>(
    context: Context, attrs: AttributeSet)
    : LinkageValueBehavior<V>(context, attrs) {

    override fun onApplyValue(target: View, value: Float) {
        if (target is ValueConsumer) target.supplyInterpolatedValue(value)
    }

    interface ValueConsumer {
        fun supplyInterpolatedValue(value: Float)
    }
}