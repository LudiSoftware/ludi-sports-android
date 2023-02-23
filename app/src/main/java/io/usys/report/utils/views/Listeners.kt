package io.usys.report.utils.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.materialdialogs.utils.MDUtil.getWidthAndHeight
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
fun View?.onMoveListener4(relativeLayout: RelativeLayout) {
    // Window Height and Width
    val parentWidth = relativeLayout.width
    val parentHeight = relativeLayout.height
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
                val lp = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                lp.width = 200
                lp.height = 200
                lp.leftMargin = max(0, min(newLeft, parentWidth - width))
                lp.topMargin = max(0, min(newTop, parentHeight - height))
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