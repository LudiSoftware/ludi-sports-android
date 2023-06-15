package io.usys.report.utils.ludi

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import kotlin.math.sqrt


fun Fragment.getScreenType() : ScreenType {
    return requireContext().getScreenType()
}

fun Context.getScreenType(): ScreenType {
    return DeviceScreenSizes.estimateTypeFromSize(this.screenWidthDp, this.screenHeightDp)
}

fun View.autoResizeToHalfWidth(margin: Int = 32) {
    val cardViewWidth = (this.context.screenWidth / 2) - margin // The 16 here is to account for margins or padding
    val layoutParams = this.layoutParams
    layoutParams.width = cardViewWidth
    this.layoutParams = layoutParams
}

// Extension properties for display metrics
val Context.displayMetrics: DisplayMetrics
    get() = resources.displayMetrics

val Context.screenWidth: Int
    get() = displayMetrics.widthPixels

val Context.screenHeight: Int
    get() = displayMetrics.heightPixels

val Context.density: Float
    get() = displayMetrics.density

val Context.scaledDensity: Float
    get() = displayMetrics.scaledDensity

val Context.screenDiagonalInches: Double
    get() {
        val metrics = resources.displayMetrics
        val widthInches = metrics.widthPixels.toDouble() / metrics.xdpi
        val heightInches = metrics.heightPixels.toDouble() / metrics.ydpi
        val diagonalInches = sqrt((widthInches * widthInches) + (heightInches * heightInches))
        return "%.1f".format(diagonalInches).toDouble()
    }

val Context.screenWidthDp: Int
    get() {
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        return (displayMetrics.widthPixels / density).toInt()
    }

val Context.screenHeightDp: Int
    get() {
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        return (displayMetrics.heightPixels / density).toInt()
    }

// Functions to convert dp and sp to pixels
fun Context.dpToPx(dp: Float): Int = (dp * density).toInt()

fun Context.spToPx(sp: Float): Int = (sp * scaledDensity).toInt()

// Functions to convert pixels to dp and sp
fun Context.pxToDp(px: Int): Float = px.toFloat() / density

fun Context.pxToSp(px: Int): Float = px.toFloat() / scaledDensity

// Functions to calculate the percentage of screen width and height
fun Context.percentOfWidth(percent: Float): Int = (screenWidth * percent).toInt()

fun Context.percentOfHeight(percent: Float): Int = (screenHeight * percent).toInt()

// Functions to get real metrics that include system decor elements (like the status bar)
val Context.realDisplayMetrics: DisplayMetrics
    get() {
        val metrics = DisplayMetrics()
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getRealMetrics(metrics)
        return metrics
    }

val Context.realScreenWidth: Int
    get() = realDisplayMetrics.widthPixels

val Context.realScreenHeight: Int
    get() = realDisplayMetrics.heightPixels

fun Context.realPercentOfWidth(percent: Float): Int = (realScreenWidth * percent).toInt()

fun Context.realPercentOfHeight(percent: Float): Int = (realScreenHeight * percent).toInt()
