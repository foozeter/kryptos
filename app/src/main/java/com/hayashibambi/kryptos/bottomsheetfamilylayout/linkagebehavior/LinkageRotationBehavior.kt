package com.hayashibambi.kryptos.bottomsheetfamilylayout.linkagebehavior

import android.content.Context
import android.util.AttributeSet
import android.view.View

class LinkageRotationBehavior<V: View>(
    context: Context, attrs: AttributeSet)
    :LinkageValueBehavior<V>(context, attrs) {

    override fun onSupplyInterpolatedValue(target: View, value: Float) {
        target.rotation = value
    }
}