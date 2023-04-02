package io.usys.report.ui.ludi.roster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.findRosterById
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.realm
import io.usys.report.ui.ludi.player.sortByOrderIndex
import io.usys.report.ui.onClickReturnStringString
import io.usys.report.ui.onClickReturnViewT
import io.usys.report.ui.views.listAdapters.RouterViewHolder
import io.usys.report.ui.views.touchAdapters.*
import io.usys.report.utils.views.wiggleOnce
import org.jetbrains.anko.sdk27.coroutines.onLongClick


/**
 * Custom Roster Setups
 */
fun LudiRosterRecyclerView?.setupRosterGridArrangable(id: String, onPlayerClick: ((View, PlayerRef) -> Unit)?, viewPager2: ViewPager2) {
    val roster = realm().findRosterById(id)
    val players: RealmList<PlayerRef> = roster?.players?.sortByOrderIndex() ?: RealmList()
    players.let {
        val adapter = RosterListAdapter(it, onPlayerClick, "medium_grid", id)
        // Drag and Drop
        val itemTouchListener = RosterDragDropAction(adapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchListener)
        // Extra
        val touchListener = RosterItemTouchListener(viewPager2)
        //Attachments
        itemTouchHelper.attachToRecyclerView(this)
        // RecyclerView
        this?.addOnItemTouchListener(touchListener)
        this?.layoutManager = GridLayoutManager(this!!.context, 2)
        this.adapter = adapter

    }
}

/**
 * Dynamic Master RecyclerView Adapter
 */
open class RosterListAdapter<PlayerRef>(): RecyclerView.Adapter<RouterViewHolder>() {

    var gridLayoutManager: GridLayoutManager? = null
    var itemClickListener: ((View, PlayerRef) -> Unit)? = onClickReturnViewT()
    var updateCallback: ((String, String) -> Unit)? = onClickReturnStringString()
    var realmList: RealmList<PlayerRef>? = null
    var layout: Int = R.layout.card_player_medium_grid
    var type: String = FireTypes.PLAYERS
    var size: String = "medium_grid"
    var rosterId: String? = null

    constructor(realmList: RealmList<PlayerRef>?, itemClickListener: ((View, PlayerRef) -> Unit)?, size: String) : this() {
        this.realmList = realmList
        this.itemClickListener = itemClickListener
        this.size = size
        this.layout = RouterViewHolder.getLayout(type, size)
    }

    constructor(realmList: RealmList<PlayerRef>?, itemClickListener: ((View, PlayerRef) -> Unit)?, size: String, rosterId:String) : this() {
        this.realmList = realmList
        this.itemClickListener = itemClickListener
        this.size = size
        this.layout = RouterViewHolder.getLayout(type, size)
        this.rosterId = rosterId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RouterViewHolder(itemView, type, updateCallback, size)
    }

    override fun getItemCount(): Int {
        return realmList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RouterViewHolder, position: Int) {
        println("binding roster player: $position")
        realmList?.let { itPlayerList ->
            itPlayerList[position]?.let { it1 ->
                holder.bind(it1 as RealmObject, position=position)
                holder.itemView.onLongClick {
                    it?.wiggleOnce()
                }
            }
        }
    }

}







