package io.usys.report.utils

import android.R
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

/** -> TRIED AND TRUE! <- */
//fun RecyclerView.init<T>(listOfArticles: MutableList<T>, fragmentActivity: FragmentActivity) {
//    val adapter = ListAdapter()
//    this.layoutManager = LinearLayoutManager(MainGlewMeTvActivity.context, LinearLayoutManager.VERTICAL, false)
//    return this.adapter
//}

//fun RecyclerView.initEvents(listOfEvents: MutableList<Event>) : EventListAdapter {
//    val eventAdapter = EventListAdapter(listOfEvents = listOfEvents)
//    this.layoutManager = LinearLayoutManager(MainGlewMeTvActivity.context, LinearLayoutManager.HORIZONTAL, false)
//    this.adapter = eventAdapter
//    return eventAdapter
//}
//
//fun RecyclerView.initTickers(listOfTickers: MutableList<Ticker>) : TickerAdapter {
//    val adapter = TickerAdapter()
//    this.layoutManager = LinearLayoutManager(MainGlewMeTvActivity.context, LinearLayoutManager.HORIZONTAL, false)
//    this.adapter = adapter
//    adapter.addListOfTickers(listOfTickers)
//    return adapter
//}
fun View.getTextView(res: Int) : TextView {
    return this.findViewById(res)
}

fun View.getRecyclerView(res: Int) : RecyclerView {
    return this.findViewById(res)
}

fun View.getImageButton(res: Int) : ImageButton {
    return this.findViewById(res)
}

fun View.getButton(res: Int) : Button {
    return this.findViewById(res)
}

fun View.getLinearLayout(res: Int) : LinearLayout {
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