package io.usys.report.ui

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import io.realm.RealmObject
import io.usys.report.utils.MyDragShadowBuilder
import io.usys.report.utils.log

/**
 * Master OnClicks
 */
fun onClickReturnEmpty(): (() -> Unit)? { return null }
fun onClickReturnViewRealmObject(): ((View, RealmObject) -> Unit)? { return null }
fun onClickReturnAnyAny(): ((Any, Any) -> Unit)? { return null }
fun onClickReturnStringString(): ((String, String) -> Unit)? { return null }
//fun onClickReturnStringString(): ((String, String) -> Unit)? { return null }

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