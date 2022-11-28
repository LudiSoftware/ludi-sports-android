package io.usys.report.utils

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by ChazzCoin : October 2022.
 */

fun Activity.changeStatusBarColor() {
    this.window.statusBarColor = this.getColorCompat(R.color.black)
}

fun <T: View> Dialog.bind(res: Int) : T {
    return this.findViewById(res)
}

fun <T: View> View.bind(res: Int) : T {
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

internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

