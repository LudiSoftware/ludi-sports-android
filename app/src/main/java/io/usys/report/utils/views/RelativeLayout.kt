package io.usys.report.utils.views

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

fun RelativeLayout.takeScreenshot(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}

fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(left, top, right, bottom)
        layoutParams = params
    }
}

fun View.centerHorizontally() {
    val parent = (parent as? RelativeLayout) ?: return
    val params = layoutParams as RelativeLayout.LayoutParams
    params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
    layoutParams = params
}

fun View.centerVertically() {
    val parent = (parent as? RelativeLayout) ?: return
    val params = layoutParams as RelativeLayout.LayoutParams
    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
    layoutParams = params
}

fun View.alignParentTop() {
    val params = layoutParams as RelativeLayout.LayoutParams
    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
    layoutParams = params
}

fun View.alignParentBottom() {
    val params = layoutParams as RelativeLayout.LayoutParams
    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
    layoutParams = params
}

fun View.alignParentLeft() {
    val params = layoutParams as RelativeLayout.LayoutParams
    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
    layoutParams = params
}

fun View.alignParentRight() {
    val params = layoutParams as RelativeLayout.LayoutParams
    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
    layoutParams = params
}
