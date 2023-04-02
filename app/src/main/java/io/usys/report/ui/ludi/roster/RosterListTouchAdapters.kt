package io.usys.report.ui.views.touchAdapters

import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.usys.report.realm.findPlayerRefById
import io.usys.report.realm.model.PlayerRef
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
class RosterDragDropAction(private val adapter: RosterListAdapter<*>) :  ItemTouchHelper.Callback() {

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

    override fun chooseDropTarget(selected: RecyclerView.ViewHolder, dropTargets: MutableList<RecyclerView.ViewHolder>, curX: Int, curY: Int): RecyclerView.ViewHolder {
        return super.chooseDropTarget(selected, dropTargets, curX, curY)
    }
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        log("onSelectedChanged: RosterId = ${adapter.rosterId}")
    }
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        log("onMove: RosterId = ${adapter.rosterId}")
        return true
    }
    override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int, target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
        // Update orderIndex of the swapped objects
        adapter.swapRealmObjects(viewHolder, target)
        //todo: fix this part!!
//        realm().safeWrite {
//            val fromPlayerRef = adapter.realmList?.get(fromPos) as? PlayerRef
//            val toPlayerRef = adapter.realmList?.get(toPos) as? PlayerRef
//            fromPlayerRef?.orderIndex = toPlayerRef?.orderIndex
//            toPlayerRef?.orderIndex = fromPlayerRef?.orderIndex
//        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // not implemented
    }
}


fun <T> RosterListAdapter<T>.swapRealmObjects(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
    val realmInstance = realm()
    val fromPosition = viewHolder.adapterPosition
    val toPosition = target.adapterPosition
    realmInstance.safeWrite {
        this.realmList?.let { it1 ->
            Collections.swap(it1, fromPosition, toPosition)
        }
    }
    this.notifyItemMoved(fromPosition, toPosition)
}

fun <T> RosterListAdapter<T>.swapRealmObjects1(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
    val realmInstance = realm()
    val fromPosition = viewHolder.adapterPosition
    val toPosition = target.adapterPosition
    val fromColumn = this.gridLayoutManager?.spanSizeLookup?.getSpanIndex(fromPosition, this.gridLayoutManager!!.spanCount)
    val toColumn = this.gridLayoutManager?.spanSizeLookup?.getSpanIndex(toPosition, this.gridLayoutManager!!.spanCount)
    if (fromColumn == toColumn) {
        // Move the item within the same column
        realmInstance.safeWrite { this.realmList?.let { it1 -> Collections.swap(it1, fromPosition, toPosition) } }
        this.notifyItemMoved(fromPosition, toPosition)
    } else {
        // Move the item to a different column
        realmInstance.safeWrite { this.realmList?.let { it1 -> Collections.swap(it1, fromPosition, toPosition) } }
        this.notifyItemChanged(fromPosition)
        this.notifyItemChanged(toPosition)
    }
}




