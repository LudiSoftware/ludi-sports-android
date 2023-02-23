package io.usys.report.utils.views

import android.content.ClipData
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView


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
