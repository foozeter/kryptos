package com.hayashibambi.kryptos

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior

class HomeActivity : AppCompatActivity() {

    private fun <V: View> AppCompatActivity.bindView(id: Int) = lazy { findViewById<V>(id) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
        setContentView(R.layout.arrow_test)
    }

    private fun buildSheetBehavior() {
//        sheetBehavior = HostBottomSheetBehavior.from(fragment_container)
//        root_layout.post {
//            sheetBehavior?.peekHeight = fragment_container.height - menu.height
//        }
    }

    private class SheetCallback: BottomSheetBehavior.BottomSheetCallback() {

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            Log.d("mylog", "onSlide offset=$slideOffset")
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            Log.d("mylog", "state changed")
        }
    }
}
