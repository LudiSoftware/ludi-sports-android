package io.usys.report.utils

import android.R
import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by ChazzCoin : December 2019.
 */

fun Activity.changeStatusBarColor() {
    this.window.statusBarColor = this.getColorCompat(R.color.black)
}

fun <T: View> View.bind(res: Int) : T {
    return this.findViewById(res)
}

fun View.bindTextView(res: Int) : TextView {
    return this.findViewById(res)
}

fun View.bindRecyclerView(res: Int) : RecyclerView {
    return this.findViewById(res)
}

fun View.bindImageButton(res: Int) : ImageButton {
    return this.findViewById(res)
}

fun View.bindSpinner(res: Int) : Spinner {
    return this.findViewById(res)
}

fun View.bindButton(res: Int) : Button {
    return this.findViewById(res)
}

fun View.bindLinearLayout(res: Int) : LinearLayout {
    return this.findViewById(res)
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

fun dpToPx(dp: Int, context: Context): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
        context.resources.displayMetrics
    ).toInt()

fun getSimpleSpinnerAdapter(context: Context, list:ArrayList<String?>) : ArrayAdapter<String?> {
    return ArrayAdapter(context, R.layout.simple_list_item_1, list)
}

fun getSpinnerForFoodTruckType(context: Context) : ArrayAdapter<String?> {
    return ArrayAdapter(context, android.R.layout.simple_list_item_1,
        context.resources.getStringArray(io.usys.report.R.array.foodtruck_types))
}

internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

fun GradientDrawable.setCornerRadius(
    topLeft: Float = 0F,
    topRight: Float = 0F,
    bottomRight: Float = 0F,
    bottomLeft: Float = 0F
) {
    cornerRadii = arrayOf(
        topLeft, topLeft,
        topRight, topRight,
        bottomRight, bottomRight,
        bottomLeft, bottomLeft
    ).toFloatArray()
}

fun showFailedToast(context: Context, mess: String = "There was an Error.") {
    Toast.makeText(context, mess, Toast.LENGTH_SHORT).show()
}

fun showSuccess(context: Context, mess: String = "Success!") {
    Toast.makeText(context, mess, Toast.LENGTH_SHORT).show()
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}