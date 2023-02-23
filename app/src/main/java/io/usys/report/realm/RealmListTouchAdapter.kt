package io.usys.report.realm

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Ability to Re-Arrange Items in RecyclerView
 */


class RealmListTouchAdapter(private val adapter: RealmListAdapter<*>) : ItemTouchHelper.Callback() {

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

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        val fromColumn = adapter.gridLayoutManager?.spanSizeLookup?.getSpanIndex(fromPosition, adapter.gridLayoutManager!!.spanCount)
        val toColumn = adapter.gridLayoutManager?.spanSizeLookup?.getSpanIndex(toPosition, adapter.gridLayoutManager!!.spanCount)
        if (fromColumn == toColumn) {
            // Move the item within the same column
            executeRealmOnMain { Collections.swap(adapter.realmList, fromPosition, toPosition) }
            adapter.notifyItemMoved(fromPosition, toPosition)
        } else {
            // Move the item to a different column
            executeRealmOnMain { Collections.swap(adapter.realmList, fromPosition, toPosition) }
            adapter.notifyItemChanged(fromPosition)
            adapter.notifyItemChanged(toPosition)
        }
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // not implemented
    }
}
