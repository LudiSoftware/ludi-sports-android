package io.usys.report.ui.gestures

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.log
import kotlin.math.sqrt

fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val dx = x2 - x1
    val dy = y2 - y1
    return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
}

class LudiFreeFormGestureDetector(context: Context, private val actionCallback: (MotionEvent?) -> Unit) :
    View.OnTouchListener {

    private val gestureDetector: GestureDetector = GestureDetector(context, GestureListener())
    private var lastAction = MotionEvent.ACTION_UP
    private var xDelta = 0f
    private var yDelta = 0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val x = event.rawX
        val y = event.rawY
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
                xDelta = x - layoutParams.leftMargin
                yDelta = y - layoutParams.topMargin
                lastAction = MotionEvent.ACTION_DOWN
            }
            MotionEvent.ACTION_MOVE -> {
                val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.leftMargin = (x - xDelta).toInt()
                layoutParams.topMargin = (y - yDelta).toInt()
                view.layoutParams = layoutParams
                lastAction = MotionEvent.ACTION_MOVE
            }
        }
        // Pass the event to the GestureDetector for handling other gestures
        gestureDetector.onTouchEvent(event)
        return true
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            actionCallback(e)
            return super.onSingleTapUp(e)
        }
        override fun onDoubleTap(event: MotionEvent?): Boolean {
            // Handle double-tap events if needed
            return super.onDoubleTap(event)
        }

        override fun onLongPress(event: MotionEvent?) {
            // Handle long-press events if needed
            super.onLongPress(event)
        }


        // Implement other gesture callbacks as needed
    }
}
//private val actionCallback: (MotionEvent?) -> Unit
class LudiOnTouchListener : View.OnTouchListener {

    private var lastAction = MotionEvent.ACTION_UP
    private var xDelta = 0f
    private var yDelta = 0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val x = event.rawX
        val y = event.rawY
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
                xDelta = x - layoutParams.leftMargin
                yDelta = y - layoutParams.topMargin
                lastAction = MotionEvent.ACTION_DOWN
            }
            MotionEvent.ACTION_MOVE -> {
                val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.leftMargin = (x - xDelta).toInt()
                layoutParams.topMargin = (y - yDelta).toInt()
                view.layoutParams = layoutParams
                lastAction = MotionEvent.ACTION_MOVE
            }
        }
        return true
    }

}