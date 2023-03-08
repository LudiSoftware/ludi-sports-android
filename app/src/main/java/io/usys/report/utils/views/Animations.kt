package io.usys.report.utils.views

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import io.usys.report.R


fun View.wiggleOnce() {
    val rotation = ObjectAnimator.ofFloat(this, View.ROTATION, 0f, 10f, -10f, 6f, -6f, 3f, -3f, 0f)
    rotation.duration = 500
    rotation.repeatCount = 0
    rotation.start()
}
fun View.wiggleShort() {
    val rotation = ObjectAnimator.ofFloat(this, View.ROTATION, 0f, 10f, -10f, 6f, -6f, 3f, -3f, 0f)
    rotation.duration = 500
    rotation.interpolator = AccelerateInterpolator()
    rotation.repeatCount = 1
    rotation.repeatMode = ObjectAnimator.REVERSE
    rotation.start()
}
fun View.wiggleLong() {
    val rotation = ObjectAnimator.ofFloat(this, View.ROTATION, 0f, 10f, -10f, 6f, -6f, 3f, -3f, 0f)
    rotation.duration = 500
    rotation.interpolator = AccelerateInterpolator()
    rotation.repeatCount = 8
    rotation.repeatMode = ObjectAnimator.REVERSE
    rotation.start()
}

inline fun ImageButton.animateOnClickListener(crossinline onClick: (View) -> Unit) {
    val anim = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", 1.2f),
        PropertyValuesHolder.ofFloat("scaleY", 1.2f)
    )

    anim.duration = 100
    anim.repeatCount = 1
    anim.repeatMode = ObjectAnimator.REVERSE

    this.setOnClickListener {
        anim.start()
        this.isClickable = false
        this.postDelayed({ this.isClickable = true }, anim.duration)
        onClick(it)
    }
}

inline fun ImageView.animateOnClickListener(crossinline onClick: (View) -> Unit) {
    val anim = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", 1.2f),
        PropertyValuesHolder.ofFloat("scaleY", 1.2f)
    )

    anim.duration = 100
    anim.repeatCount = 1
    anim.repeatMode = ObjectAnimator.REVERSE

    this.setOnClickListener {
        anim.start()
        this.isClickable = false
        this.postDelayed({ this.isClickable = true }, anim.duration)
        onClick(it)
    }
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