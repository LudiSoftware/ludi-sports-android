package io.usys.report.utils.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import io.usys.report.R

fun GradientDrawable.setCornerRadius(topLeft: Float = 0F, topRight: Float = 0F, bottomRight: Float = 0F, bottomLeft: Float = 0F) {
    cornerRadii = arrayOf(topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft).toFloatArray()
}

internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)

fun getDrawable(context: Context?, drawable: Int): Drawable? {
    return context?.getDrawableCompat(drawable)
}

fun getColor(context: Context, colorResource:Int): Int {
    return ContextCompat.getColor(context, colorResource)
}

fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return bitmap
    }

    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)

    return bitmap
}
