package io.usys.report.ui.views.touchAdapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.ui.views.listAdapters.rosterLiveList.RosterListLiveAdapter
import io.usys.report.utils.log
import java.util.*

/** Working **/
class RosterLiveDragDropAction(private val adapter: RosterListLiveAdapter) :  ItemTouchHelper.Callback() {
    val realmInstance = realm()

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        adapter.updateOrderIndexes()
        adapter.softRefresh()
        log("clearView: RosterId = ${adapter.config.rosterId}")
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        log("onSelectedChanged: RosterId = ${adapter.config.rosterId}")
    }
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        log("onMove: RosterId = ${adapter.config.rosterId}")
        return true
    }
    override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int, target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
        // Update orderIndex of the swapped objects
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        realmInstance.safeWrite {
            adapter.itemList?.let { realmList ->
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(realmList, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(realmList, i, i - 1)
                    }
                }
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition)
        log("onMoved: RosterId = ${adapter.config.rosterId}")
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // not implemented
    }
}


