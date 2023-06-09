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
            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (isSwipeRightToLeft(e1, e2, velocityX, null)) {
                    onNavSwipe?.invoke("left")
                    return true
                }

                if (isSwipeLeftToRight(e1, e2, velocityX, null)) {
                    onNavSwipe?.invoke("right")
                    return true
                }
                return true
            }
        })

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return event?.let { gestureDetector.onTouchEvent(it) } ?: true
    }
}