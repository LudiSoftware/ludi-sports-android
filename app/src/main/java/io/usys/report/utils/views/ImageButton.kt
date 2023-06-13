package io.usys.report.utils.views

import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.TooltipCompat
import com.bumptech.glide.Glide
import io.usys.report.R

fun ImageButton.disable() {
    this.isEnabled = false
}
fun ImageButton.enable() {
    this.isEnabled = true
}
fun ImageButton.hide() {
    this.visibility = View.GONE
}
fun ImageButton.show() {
    this.visibility = View.VISIBLE
}
fun ImageButton.toggleVisibility() {
    this.visibility = if (this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
}
fun ImageButton.rotate(degrees: Float) {
    this.rotation = degrees
}
fun ImageButton.setDebounceClickListener(debounceTime: Long = 300L, onClick: () -> Unit) {
    this.setOnClickListener(object: View.OnClickListener {
        private var lastClickTime = 0L
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else onClick.invoke()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}
fun ImageButton.showTooltip(message: String) {
    TooltipCompat.setTooltipText(this, message)
}
fun ImageButton.loadImage(url: String, errorDrawable: Int) {
    Glide.with(this.context).load(url).error(errorDrawable).into(this)
}

fun ImageButton.startScaleAnimation() {
    val anim = ScaleAnimation(
        1f, 1.2f,
        1f, 1.2f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.duration = 500
    anim.repeatCount = Animation.INFINITE
    anim.repeatMode = Animation.REVERSE
    this.startAnimation(anim)
}

@SuppressLint("ClickableViewAccessibility")
fun ImageButton.changeAlphaOnPress(pressedAlpha: Float = 0.6f) {
    this.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> this.alpha = pressedAlpha
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> this.alpha = 1f
        }
        false
    }
}

fun ImageButton.startShakeAnimation() {
    val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
    this.startAnimation(shake)
}

fun ImageButton.startFlashingAnimation() {
    val anim = AlphaAnimation(0.0f, 1.0f)
    anim.duration = 500
    anim.startOffset = 20
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE
    this.startAnimation(anim)
}






