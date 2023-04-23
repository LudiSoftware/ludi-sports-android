package io.usys.report.ui.views.gestures

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat

class HorizontalFlingDetector(context: Context, onNavSwipe: ((String) -> Unit)?) : View.OnTouchListener {

    private val gestureDetector: GestureDetectorCompat =
        GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                e1?.let { event1 ->
                    e2?.let { event2 ->

                        if (isSwipeRightToLeft(event1, event2, velocityX, null)) {
                            onNavSwipe?.invoke("left")
                            return true
                        }

                        if (isSwipeLeftToRight(event1, event2)) {
                            onNavSwipe?.invoke("right")
                            return true
                        }
                    }
                }
                return true
            }
        })

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return event?.let { gestureDetector.onTouchEvent(it) } ?: true
    }
}