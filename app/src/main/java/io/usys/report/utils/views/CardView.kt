package io.usys.report.utils.views

import android.content.res.ColorStateList
import android.view.View
import androidx.annotation.ColorRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

fun CardView.setCardCornerRadius(radius: Float) {
    this.radius = radius
}

fun CardView.preventCornerOverlap() {
    this.preventCornerOverlap = true
}
fun CardView.maximizeCardElevation() {
    this.maxCardElevation = this.cardElevation
}
fun CardView.setRippleColor(@ColorRes colorRes: Int) {
    this.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
}
fun CardView.setCardMaxElevation(elevation: Float) {
    this.maxCardElevation = elevation
}
fun CardView.hide() {
    this.visibility = View.GONE
}
fun CardView.show() {
    this.visibility = View.VISIBLE
}
fun CardView.enable() {
    this.isEnabled = true
}
fun CardView.disable() {
    this.isEnabled = false
}
fun CardView.toggleVisibility() {
    this.visibility = if (this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
}
