package io.usys.report.ui.views.listAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
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
import io.usys.report.ui.views.LudiRosterView
import io.usys.report.ui.views.touchAdapters.RosterListTouchAdapter
import io.usys.report.utils.isNullOrEmpty

fun LudiRosterView?.setupRosterGridArrangable(id: String, onPlayerClick: ((View, PlayerRef) -> Unit)?) {
    val roster = realm().findRosterById(id)
    val players: RealmList<PlayerRef> = roster?.players?.sortByOrderIndex() ?: RealmList()
    players.let {
        this?.loadInRosterGridArrangable(it, onPlayerClick, "medium_grid")
    }
}

fun LudiRosterView.loadInRosterGridArrangable(realmList: RealmList<PlayerRef>?,
                                              itemOnClick: ((View, PlayerRef) -> Unit)?,
                                              size:String="medium_grid") {
    if (realmList.isNullOrEmpty()) return
    val adapter = RosterListAdapter(realmList, itemOnClick, size)
    this.layoutManager = GridLayoutManager(this.context, 2)
    this.adapter = adapter
    val itemTouchHelper = ItemTouchHelper(RosterListTouchAdapter(adapter))
    itemTouchHelper.attachToRecyclerView(this)
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

    constructor(realmList: RealmList<PlayerRef>?, itemClickListener: ((View, PlayerRef) -> Unit)?, size: String) : this() {
        this.realmList = realmList
        this.itemClickListener = itemClickListener
        this.size = size
        this.layout = RouterViewHolder.getLayout(type, size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RouterViewHolder(itemView, type, updateCallback, size)
    }

    override fun getItemCount(): Int {
        return realmList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RouterViewHolder, position: Int) {
        println("binding realmlist")
        realmList?.let {
            it[position]?.let { it1 ->
                holder.bind(it1 as RealmObject, position=position)
                holder.itemView.setOnRealmListener(itemClickListener, it1)
            }
        }
    }

}








