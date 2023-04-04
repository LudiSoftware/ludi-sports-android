package io.usys.report.ui.views.touchAdapters

import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.usys.report.firebase.fireUpdatePlayersInRoster
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.ui.ludi.roster.RosterListAdapter
import io.usys.report.utils.log
import java.util.*


class RosterItemTouchListener(private val viewPager2: ViewPager2) : RecyclerView.OnItemTouchListener {
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                viewPager2.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                viewPager2.requestDisallowInterceptTouchEvent(true)
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        viewPager2.requestDisallowInterceptTouchEvent(disallowIntercept)
    }
}


/** Working **/
class RosterDragDropAction(private val adapter: RosterListAdapter) :  ItemTouchHelper.Callback() {
    val realmInstance = realm()
//    var fromPosition = 0
//    var toPosition = 0

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
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        adapter.updateOrderIndexes()
        adapter.notifyDataSetChanged()
        //TODO: Update firebase Manually
        fireUpdatePlayersInRoster(adapter.config.rosterId!!, adapter.playerRefList)
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
            adapter.playerRefList?.let { realmList ->
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




