package io.usys.report.utils.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.materialdialogs.utils.MDUtil.getWidthAndHeight
import io.usys.report.ui.ludi.player.popPlayerProfileDialog
import io.usys.report.utils.log
import kotlin.math.max
import kotlin.math.min

/**
 * Ability to Drag and Drop ImageViews freely
 * Only works with a RelativeLayout!
 */
@SuppressLint("ClickableViewAccessibility")
fun View?.onMoveListener(window: Window?) {
    // Window Height and Width
    val windowWidthAndHeight = window?.windowManager?.getWidthAndHeight()
    val windowWidth = windowWidthAndHeight?.first ?: 0
    val windowHeight = windowWidthAndHeight?.second ?: 0
    // OnTouch
    this?.setOnTouchListener { _, event ->
        val layoutParams: RelativeLayout.LayoutParams = this.layoutParams as RelativeLayout.LayoutParams
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_MOVE -> {
                var x_cord = event.rawX.toInt()
                var y_cord = event.rawY.toInt()
                if (x_cord > windowWidth) {
                    x_cord = windowWidth
                }
                if (y_cord > windowHeight) {
                    y_cord = windowHeight
                }
                layoutParams.leftMargin = x_cord - 100
                layoutParams.topMargin = y_cord - 200
                this.layoutParams = layoutParams
            }
            else -> {}
        }
        true
    }
}

@SuppressLint("ClickableViewAccessibility")
fun View?.onMoveListenerRosterFormation(height:Int=200, width: Int=200) {
    // Window Height and Width
    var lastX = 0
    var lastY = 0
    // OnTouch
    this?.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Save the last touch position
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                // Calculate the amount of movement since the last touch
                val dx = event.rawX.toInt() - lastX
                val dy = event.rawY.toInt() - lastY
                // Calculate the new position of the child view
                val newLeft = left + dx
                val newTop = top + dy

                // Make sure the child view doesn't go outside the bounds of the parent layout
                val lp = getRelativeLayoutParams()
                lp.width = width
                lp.height = height
                //todo: Save the (x,y) coordinates of the view in the RealmObject/Firebase.
                //      - Then load in the coordinates when the view is created.
                lp.leftMargin = newLeft
                lp.topMargin = newTop
                layoutParams = lp

                // Save the last touch position
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
            }
            else -> {}
        }
        true
    }
}

fun View?.onMoveListenerRosterFormation2(height: Int = 200, width: Int = 200, onSingleTapUp: (() -> Unit)? = null) {
    // Window Height and Width
    var lastX = 0
    var lastY = 0
    val viewObject: View? = this

    // Create the gesture detector
    val gestureDetector = GestureDetector(this?.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            onSingleTapUp?.invoke()
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            // Handle the scroll event
            val dx = e2.rawX.toInt() - lastX
            val dy = e2.rawY.toInt() - lastY

            // Calculate the new position of the child view
            val newLeft = viewObject!!.left + dx
            val newTop = viewObject.top + dy

            // Make sure the child view doesn't go outside the bounds of the parent layout
            val lp = getRelativeLayoutParams()
            lp.width = width
            lp.height = height
            lp.leftMargin = newLeft
            lp.topMargin = newTop
            viewObject.layoutParams = lp

            // Save the last touch position
            lastX = e2.rawX.toInt()
            lastY = e2.rawY.toInt()
            return true
        }
    })

    // Set the touch listener to pass events to the gesture detector
    this?.setOnTouchListener { _, event ->
        gestureDetector.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Save the last touch position
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
            }
            else -> {}
        }
        true
    }
}

