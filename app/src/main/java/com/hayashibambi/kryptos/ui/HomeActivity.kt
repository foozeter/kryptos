package com.hayashibambi.kryptos.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hayashibambi.kryptos.R

class HomeActivity : AppCompatActivity() {

    private fun <V: View> AppCompatActivity.bindView(id: Int) = lazy { findViewById<V>(id) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

}
