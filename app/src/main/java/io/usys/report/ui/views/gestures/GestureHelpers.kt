package io.usys.report.ui.views.gestures

import android.view.MotionEvent
import kotlin.math.abs
import kotlin.math.atan2

/**
 * Helpers to:
 *      Return the scroll direction of the MotionEvent
 */
fun MotionEvent.getScrollDirection(event2: MotionEvent): String {
    val deltaX = event2.x - x
    val deltaY = event2.y - y
    val angle = Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble()))
    return when {
        angle > -45 && angle <= 45 -> "right" // Right
        angle > 45 && angle <= 135 -> "up" // Up
        angle > 135 || angle <= -135 -> "left" // Left
        else -> "down" // Down
    }
}

fun MotionEvent.isUpScroll(): Boolean {
    return getScrollDirection(this) == "up"
}

fun MotionEvent.isDownScroll(): Boolean {
    return getScrollDirection(this) == "down"
}

fun MotionEvent.isLeftScroll(): Boolean {
    return getScrollDirection(this) == "left"
}

fun MotionEvent.isRightScroll(): Boolean {
    return getScrollDirection(this) == "right"
}

fun isSwipeLeftToRight(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, callback:(()-> Unit)?) : Boolean {
    val swipeThreshold = 100
    val swipeVelocityThreshold = 100
    val deltaX = e2!!.x - e1!!.x
    val deltaY = e2.y - e1.y
    if (abs(deltaX) > abs(deltaY) && abs(deltaX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
        // Swipe started from left edge and went to the right
        if (e1.x < 100 && deltaX > 0) {
            callback?.invoke()
            return true
        }
    }
    return false
}

fun isSwipeRightToLeft(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, callback:(()-> Unit)?) : Boolean {
    val swipeThreshold = 100
    val swipeVelocityThreshold = 100
    val deltaX = e2!!.x - e1!!.x
    val deltaY = e2.y - e1.y
    if (abs(deltaX) > abs(deltaY) && abs(deltaX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
        // Swipe started from right edge and went to the left
        if (e1.x > (e1.rawX - 100) && deltaX < 0) {
            callback?.invoke()
            return true
        }
    }
    return false
}