package com.hayashibambi.kryptos

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior

class HomeActivity : AppCompatActivity() {

    private var sheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
//        buildSheetBehavior()

        setContentView(R.layout.arrow_test)
//        val arrow = UpDownArrowDrawable(this)
//        arrow.isUpToDown = false
        findViewById<View>(R.id.test_view).apply {
            setOnClickListener { view ->
                Toast.makeText(this@HomeActivity, "start the animation!", Toast.LENGTH_SHORT).show()
                val anim = ObjectAnimator.ofFloat(0f, 1f)
                anim.duration = 5000
                anim.addUpdateListener {
                    (view as UpDownArrow).setProgress(it.animatedFraction)
//                    ViewCompat.postInvalidateOnAnimation(view)
                }
                anim.start()
            }
        }
    }

    private fun buildSheetBehavior() {
//        sheetBehavior = BottomSheetBehavior.from(fragment_container)
//        root_layout.post {
//            sheetBehavior?.peekHeight = fragment_container.height - menu.height
//        }
    }
}
