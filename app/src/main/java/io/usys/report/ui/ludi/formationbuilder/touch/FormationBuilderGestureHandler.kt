package io.usys.report.ui.ludi.formationbuilder.touch



import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs
import kotlin.math.atan2


/**
 * -> Master Handler for all gestures in the FormationBuilderFragment
 * @param context The context
 * @param motionLayout The MotionLayout
 *
 */
class FormationBuilderGestureHandler(context: Context, private val motionLayout: MotionLayout) : View.OnTouchListener {

    private val gestureDetector: GestureDetectorCompat =
        GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                // Handle single tap event here
                if (motionLayout.progress > 0.8f) {
                    motionLayout.transitionToStart()
                }
                return true
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                // Handle scroll events (swipe up/down) here
                if (e1!!.isUpScroll()) {
                    motionLayout.transitionToEnd()
                } else if (e1.isDownScroll()) {
                    motionLayout.transitionToStart()
                }
                return true
            }

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                e1?.let { event1 ->
                    e2?.let { event2 ->
                        val deltaY = event2.y - event1.y
                        val deltaX = event2.x - event1.x
                        val isVerticalSwipe = abs(deltaY) > abs(deltaX)
                        if (isVerticalSwipe) {
                            if (deltaY < 0) {
                                // Handle fling up
                                motionLayout.transitionToEnd()
                            } else {
                                // Handle fling down
                                motionLayout.transitionToStart()
                            }
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