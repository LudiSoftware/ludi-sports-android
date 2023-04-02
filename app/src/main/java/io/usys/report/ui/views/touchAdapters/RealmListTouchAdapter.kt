package io.usys.report.ui.views.touchAdapters

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.realm.writeToRealmOnMain
import io.usys.report.ui.ludi.roster.RosterListAdapter
import io.usys.report.ui.views.listAdapters.RealmListAdapter
import io.usys.report.ui.views.viewGroup.TouchSensitiveTabLayout
import io.usys.report.utils.views.wiggleOnce
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
        adapter.swapRealmObjects(viewHolder, target)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // not implemented
    }
}


fun <T> RealmListAdapter<T>.swapRealmObjects(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
    val fromPosition = viewHolder.adapterPosition
    val toPosition = target.adapterPosition
    val fromColumn = this.gridLayoutManager?.spanSizeLookup?.getSpanIndex(fromPosition, this.gridLayoutManager!!.spanCount)
    val toColumn = this.gridLayoutManager?.spanSizeLookup?.getSpanIndex(toPosition, this.gridLayoutManager!!.spanCount)
    if (fromColumn == toColumn) {
        // Move the item within the same column
        writeToRealmOnMain { Collections.swap(this.realmList, fromPosition, toPosition) }
        this.notifyItemMoved(fromPosition, toPosition)
    } else {
        // Move the item to a different column
        writeToRealmOnMain { Collections.swap(this.realmList, fromPosition, toPosition) }
        this.notifyItemChanged(fromPosition)
        this.notifyItemChanged(toPosition)
    }
}