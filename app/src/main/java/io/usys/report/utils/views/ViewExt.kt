package io.usys.report.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by ChazzCoin : October 2022.
 */
fun View.slideUp(duration: Long = 10) {
    this.animate()
        .translationY(-this.height.toFloat())
        .setInterpolator(AccelerateInterpolator())
        .setDuration(duration)
        .withEndAction { this.visibility = View.GONE }
        .start()
}

fun View.slideDown(duration: Long = 10) {
    this.visibility = View.VISIBLE
    this.animate()
        .translationY(0f)
        .setInterpolator(DecelerateInterpolator())
        .setDuration(duration)
        .start()
}
fun View.animateVisible(visible: Boolean, duration: Long = 200) {
    animate().setDuration(duration).alpha(if (visible) 1f else 0f).start()
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}
// INFLATERS
fun inflateView(context: Context, @LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, null, attachToRoot)
}
fun ViewGroup?.inflateLayout(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(this?.context).inflate(layoutRes, this, attachToRoot)
}

fun View.setBackgroundImage(@DrawableRes drawableRes: Int) {
    this.background = ContextCompat.getDrawable(context, drawableRes)
}

fun View.enablePinchToZoom() {
    val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private var scaleFactor = 1.0f

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            this@enablePinchToZoom.scaleX = scaleFactor
            this@enablePinchToZoom.scaleY = scaleFactor
            return true
        }
    })

    setOnTouchListener { _, event ->
        scaleGestureDetector.onTouchEvent(event)
        true
    }
}


inline fun View.attachViewsToOnClickListener(vararg views: View, crossinline onClick: (View) -> Unit) {
    val clickListener = View.OnClickListener { view -> onClick(view) }

    this.setOnClickListener(clickListener)
    for (view in views) {
        view.setOnClickListener(clickListener)
    }
}

fun <T: View> Dialog.bind(res: Int) : T {
    return this.findViewById(res)
}

fun <T: View> View.bind(res: Int) : T? {
    return this.findViewById(res)
}

fun View?.bindTextView(res: Int) : TextView? {
    return this?.findViewById(res)
}

fun View?.bindRecyclerView(res: Int) : RecyclerView? {
    return this?.findViewById(res)
}

fun View?.bindImageButton(res: Int) : ImageButton? {
    return this?.findViewById(res)
}

fun View?.bindSpinner(res: Int) : Spinner? {
    return this?.findViewById(res)
}

fun View?.bindButton(res: Int) : Button? {
    return this?.findViewById(res)
}

fun View?.bindLinearLayout(res: Int) : LinearLayout? {
    return this?.findViewById(res)
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun dpToPx(dp: Int, context: Context): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()


internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

