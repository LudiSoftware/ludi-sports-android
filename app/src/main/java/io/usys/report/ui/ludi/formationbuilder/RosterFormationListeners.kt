package io.usys.report.ui.ludi.formationbuilder

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import io.usys.report.realm.findByField
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.realm.realm
import io.usys.report.utils.views.getRelativeLayoutParams
import kotlin.math.max
import kotlin.math.min

fun View?.onGestureDetectorRosterFormation(height:Int=200, width:Int=200, playerId:String?=null,
                                           onSingleTapUp:(() -> Unit)?=null, onLongPress:(() -> Unit)?=null) {
    // Window Height and Width
    var lastX = 0
    var lastY = 0
    val viewObject: View? = this
    val scaleGestureDetector = ScaleGestureDetector(this?.context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        var scaleFactor = 1.0f
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= detector?.scaleFactor ?: 1.0f
            scaleFactor = max(0.1f, min(scaleFactor, 5.0f))
            viewObject?.scaleX = scaleFactor
            viewObject?.scaleY = scaleFactor
            return true
        }
    })
    // Create the gesture detector
    val gestureDetector = GestureDetector(this?.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            lastX = e.rawX.toInt()
            lastY = e.rawY.toInt()
            return true
        }
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val tempRealm = realm()
            tempRealm.safeUserId { itUserId ->
                tempRealm.findByField<FormationSession>("id", itUserId)?.let { fs ->
                    playerId?.let { itId ->
                        fs.roster?.players?.find { it.id == itId }?.let { playerRef ->
                            tempRealm.executeTransaction {
                                playerRef.pointX = lastX
                                playerRef.pointY = lastY
                                it.copyToRealmOrUpdate(playerRef)
                            }
                        }
                    }
                }
            }
            onSingleTapUp?.invoke()
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return super.onDoubleTap(e)
        }

        override fun onLongPress(e: MotionEvent?) {
            onLongPress?.invoke()
        }

        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
            return super.onDoubleTapEvent(e)
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            return super.onFling(e1, e2, velocityX, velocityY)
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
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        true
    }
}