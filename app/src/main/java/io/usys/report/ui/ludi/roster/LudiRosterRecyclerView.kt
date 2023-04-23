package io.usys.report.ui.ludi.roster

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.ui.views.gestures.isSwipeLeftToRight
import io.usys.report.ui.views.gestures.isSwipeRightToLeft
import io.usys.report.utils.log

class LudiRosterRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

    private var gestureDetector: GestureDetector

    // Define your onFlingListener here
    var onFlingListener: ((String) -> Unit)? = null
    var navController: NavController? = null

    init {
        onFlinger()
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (isSwipeRightToLeft(e1, e2, velocityX, null)) {
                    onFlingListener?.invoke("left")
                    return true
                }
                if (isSwipeLeftToRight(e1, e2, velocityX, null)) {
                    onFlingListener?.invoke("right")
                    return true
                }
                return true
            }
        })
    }

    fun onFlinger() {
        onFlingListener = {
            log("onFlingListener: $it")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        // Detect the fling gesture without interrupting the drag listener
        gestureDetector.onTouchEvent(e)
        return super.onTouchEvent(e)
    }
}


