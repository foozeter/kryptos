package com.hayashibambi.kryptos

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior

class HomeActivity : AppCompatActivity() {

    private var sheetBehavior: BottomSheetBehavior<*>? = null

    private val icon: ImageView by bindView(R.id.icon)

    private fun <V: View> AppCompatActivity.bindView(id: Int) = lazy { findViewById<V>(id) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    private fun buildSheetBehavior() {
//        sheetBehavior = BottomSheetBehavior.from(fragment_container)
//        root_layout.post {
//            sheetBehavior?.peekHeight = fragment_container.height - menu.height
//        }
    }
}
