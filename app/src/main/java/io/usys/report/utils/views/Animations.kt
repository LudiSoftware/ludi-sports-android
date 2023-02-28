package io.usys.report.utils.views

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import io.usys.report.R



fun Button.wiggle() {
    val rotation = ObjectAnimator.ofFloat(this, "rotation", 0f, 10f, -10f, 6f, -6f, 3f, -3f, 0f)
    rotation.duration = 1000
    rotation.interpolator = AccelerateInterpolator()
    rotation.repeatCount = ObjectAnimator.INFINITE
    rotation.start()
}

fun View.fadeInOut(otherView: View, duration: Long) {
    animate().alpha(0f).setDuration(duration).withEndAction {
        visibility = View.GONE
    }
    otherView.alpha = 0f
    otherView.visibility = View.VISIBLE
    otherView.animate().alpha(1f).setDuration(duration).start()
}

inline fun <reified T : View> View.findParentOfType(): T? {
    var parent = parent
    while (parent != null) {
        if (parent is T) {
            return parent
        }
        parent = parent.parent
    }
    return null
}

fun View.getTopInset(): Int {
    val windowInsets = rootWindowInsets ?: return 0
    return windowInsets.systemWindowInsetTop
}