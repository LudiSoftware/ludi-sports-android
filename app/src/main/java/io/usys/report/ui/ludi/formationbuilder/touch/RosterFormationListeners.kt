package io.usys.report.ui.ludi.formationbuilder

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.RelativeLayout
import io.usys.report.realm.findPlayersInRosterById
import io.usys.report.realm.findRosterById
import io.usys.report.realm.local.rosterSessionById
import io.usys.report.realm.local.teamSessionByTeamId
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.utils.views.getRelativeLayoutParams
import kotlin.math.max
import kotlin.math.min

fun View?.onGestureDetectorRosterFormation(rosterId:String?=null, playerId:String?=null,
                                           onSingleTapUp:((String) -> Unit)?=null,
                                           onLongPress:((String) -> Unit)?=null) {
    val tempRealm = realm()
    // Window Height and Width
    var lastX = 0
    var lastY = 0
    var topMargin = 0
    var leftMargin = 0
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
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (playerId != null) {
                onSingleTapUp?.invoke(playerId)
            }
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {

            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            if (playerId != null) {
                onLongPress?.invoke(playerId)
            }
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
            leftMargin = viewObject!!.left + dx
            topMargin = viewObject.top + dy

            // Make sure the child view doesn't go outside the bounds of the parent layout
            val lp = getRelativeLayoutParams()
            lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT
            lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT
            lp.leftMargin = leftMargin
            lp.topMargin = topMargin
            viewObject.layoutParams = lp

            tempRealm.findPlayersInRosterById(rosterId)?.let { players ->
                players.find { it.id == playerId }?.let { playerRef ->
                    tempRealm.safeWrite {
                        playerRef.pointX = topMargin
                        playerRef.pointY = leftMargin
                        it.copyToRealmOrUpdate(playerRef)
                    }
                }
            }
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