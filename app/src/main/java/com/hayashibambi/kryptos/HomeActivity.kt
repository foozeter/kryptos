package com.hayashibambi.kryptos

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.home_custom_tab.*
import kotlinx.android.synthetic.main.home_custom_tab.view.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        slash_layout.setOnTouchEventOutOfBoundsListener { pointer, ev ->
            Log.d("mylog", "touch was ignored...")
            return@setOnTouchEventOutOfBoundsListener true
        }
    }
}
