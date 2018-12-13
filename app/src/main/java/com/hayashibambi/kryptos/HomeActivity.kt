package com.hayashibambi.kryptos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var sheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        buildSheetBehavior()
    }

    private fun buildSheetBehavior() {
        sheetBehavior = BottomSheetBehavior.from(fragment_container)
        root_layout.post {
            sheetBehavior?.peekHeight = fragment_container.height - menu.height
        }
    }
}
