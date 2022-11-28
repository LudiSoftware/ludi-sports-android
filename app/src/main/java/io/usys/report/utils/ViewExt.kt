package io.usys.report.utils

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * Created by ChazzCoin : October 2022.
 */


fun Context.loadUriIntoImgView(uri: Uri, imageView: ImageView ) {
    Glide.with(this).load(uri).into(imageView)
}

fun Fragment.loadUriIntoImgView(uri: Uri, imageView: ImageView ) {
    Glide.with(this).load(uri).into(imageView)
}

fun Fragment.loadUriIntoImgView(stringUri: String, imageView: ImageView ) {
    val uri = stringUri.toUri()
    Glide.with(this).load(uri).into(imageView)
}


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


fun View.ysrOnLongClickListener() {
    setOnLongClickListener { v ->
        // Create a new ClipData.
        // This is done in two steps to provide clarity. The convenience method
        // ClipData.newPlainText() can create a plain text ClipData in one step.

        // Create a new ClipData.Item from the ImageView object's tag.
        val item = ClipData.Item(v.tag as? CharSequence)

        // Create a new ClipData using the tag as a label, the plain text MIME type, and
        // the already-created item. This creates a new ClipDescription object within the
        // ClipData and sets its MIME type to "text/plain".
        val dragData = ClipData(v.tag as? CharSequence, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)

        // Instantiate the drag shadow builder.
        val myShadow = MyDragShadowBuilder(this)

        // Start the drag.
        v.startDragAndDrop(dragData,  // The data to be dragged
            myShadow,  // The drag shadow builder
            null,      // No need to use local data
            0          // Flags (not currently used, set to 0)
        )

        // Indicate that the long-click was handled.
        true
    }
}

fun ImageView.ysrOnDragListener() {
    setOnDragListener { v, e ->

        // Handles each of the expected events.
        when (e.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                // Determines if this View can accept the dragged data.
                if (e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    // As an example of what your application might do, applies a blue color tint
                    // to the View to indicate that it can accept data.
                    (v as? ImageView)?.setColorFilter(Color.BLUE)

                    // Invalidate the view to force a redraw in the new tint.
                    v.invalidate()

                    // Returns true to indicate that the View can accept the dragged data.
                    true
                } else {
                    // Returns false to indicate that, during the current drag and drop operation,
                    // this View will not receive events again until ACTION_DRAG_ENDED is sent.
                    false
                }
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                // Applies a green tint to the View.
                (v as? ImageView)?.setColorFilter(Color.GREEN)

                // Invalidates the view to force a redraw in the new tint.
                v.invalidate()

                // Returns true; the value is ignored.
                true
            }

            DragEvent.ACTION_DRAG_LOCATION ->
                // Ignore the event.
                true
            DragEvent.ACTION_DRAG_EXITED -> {
                // Resets the color tint to blue.
                (v as? ImageView)?.setColorFilter(Color.BLUE)

                // Invalidates the view to force a redraw in the new tint.
                v.invalidate()

                // Returns true; the value is ignored.
                true
            }
            DragEvent.ACTION_DROP -> {
                // Gets the item containing the dragged data.
                val item: ClipData.Item = e.clipData.getItemAt(0)

                // Gets the text data from the item.
                val dragData = item.text

                // Displays a message containing the dragged data.
//                Toast.makeText(this, "Dragged data is $dragData", Toast.LENGTH_LONG).show()

                // Turns off any color tints.
                (v as? ImageView)?.clearColorFilter()

                // Invalidates the view to force a redraw.
                v.invalidate()

                // Returns true. DragEvent.getResult() will return true.
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                // Turns off any color tinting.
                (v as? ImageView)?.clearColorFilter()

                // Invalidates the view to force a redraw.
                v.invalidate()

                // Does a getResult(), and displays what happened.
                when(e.result) {
                    true ->
                        Toast.makeText(context, "The drop was handled.", Toast.LENGTH_LONG)
                    else ->
                        Toast.makeText(context, "The drop didn't work.", Toast.LENGTH_LONG)
                }.show()

                // Returns true; the value is ignored.
                true
            }
            else -> {
                // An unknown action type was received.
                log("Unknown action type received by View.OnDragListener.")
                false
            }
        }
    }
}