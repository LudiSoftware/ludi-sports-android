package io.usys.report.ui.ludi.formationbuilder

import android.view.DragEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView

/**
 * FORMATION LAYOUT: DROP LISTENER for when a player is dragged onto the soccer field
 */
inline fun RelativeLayout.setFormationDropListener(crossinline dropBlock: (String, Float, Float) -> Unit) {
    val dragListener = View.OnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                val x = event.x
                val y = event.y
                val clipData = event.clipData
                if (clipData != null && clipData.itemCount > 0) {
                    val playerId = clipData.getItemAt(0).text.toString()
                    dropBlock(playerId, x, y)
                }
                true
            }
            else -> {
                true
            }
        }
    }
    this.setOnDragListener(dragListener)
}

/**
 * ON-DECK LAYOUT: DRAG LISTENER for when a player is dragged off the deck.
 */
fun RecyclerView.setDeckDragListener(motionLayout: MotionLayout?=null, linearLayout: LinearLayoutCompat?=null) {
    this.setOnDragListener(object : View.OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DRAG_LOCATION -> {
                    // Check if the drag event is above the LinearLayout
                    val y = event.y.toInt()
                    if (y <= 2) {
                        println(" !!!! Drop Player Available !!!! Y=[$y], TOP=[$top]")
                        motionLayout?.transitionToStart()
                    } else {
                        println(" !!!! Can't Drop Player Here !!!! Y=[$y], TOP=[$top]")
                        return false
                    }
                }
                else -> {}
            }
            return true
        }
    })
}
