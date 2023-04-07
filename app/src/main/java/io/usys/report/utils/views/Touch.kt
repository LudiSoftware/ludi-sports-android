package io.usys.report.utils.views

import android.content.ClipData
import android.view.DragEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.ui.ludi.formationbuilder.setFormationDropListener


fun getRelativeLayoutParams(): RelativeLayout.LayoutParams {
    return RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT)
}


fun RecyclerView.ViewHolder.startClipDataDragAndDrop(objId:String, flags:Int=0): Boolean {
    val dragShadow = View.DragShadowBuilder(this.itemView)
    val clipData = ClipData.newPlainText("playerId", objId)
    return this.itemView.startDragAndDrop(clipData, dragShadow, this, flags)
}

fun View.startClipDataDragAndDrop(objId:String, flags:Int=0): Boolean {
    val dragShadow = View.DragShadowBuilder(this)
    val clipData = ClipData.newPlainText("objId", objId)
    return this.startDragAndDrop(clipData, dragShadow, this, flags)
}

inline fun View.setDropListener(crossinline dropBlock: (String, Float, Float) -> Unit) {
    val dragListener = View.OnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                val x = event.x
                val y = event.y
                val clipData = event.clipData
                if (clipData != null && clipData.itemCount > 0) {
                    val clipD = clipData.getItemAt(0).text.toString()
                    dropBlock(clipD, x, y)
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