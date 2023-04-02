package io.usys.report.ui.views.viewGroup

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.tabs.TabLayout

class TouchSensitiveTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : TabLayout(context, attrs, defStyle) {

    var shouldHandleTouchEvent: Boolean = true

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (shouldHandleTouchEvent) {
            super.onTouchEvent(ev)
        } else {
            false
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (shouldHandleTouchEvent) {
            super.onInterceptTouchEvent(ev)
        } else {
            false
        }
    }
}