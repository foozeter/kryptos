package com.hayashibambi.kryptos

import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.home_custom_tab.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        rollytabs.apply {
            val d = resources.getDrawable(R.drawable.abc_ic_star_black_16dp)
            addTab("", d)
            addTab("", d)
            addTab("", d)
        }
    }
}
