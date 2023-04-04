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
import io.usys.report.realm.model.PLAYER_STATUS_SELECTED
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.ui.ludi.player.*
import io.usys.report.ui.onClickReturnStringString
import io.usys.report.ui.onClickReturnViewT
import io.usys.report.ui.views.listAdapters.RouterViewHolder
import io.usys.report.ui.views.touchAdapters.*
import io.usys.report.utils.views.getColor
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
open class RosterListAdapter(): RecyclerView.Adapter<RouterViewHolder>() {

    // Realm
    var realmInstance = realm()
    // Touch Adapters
    var itemTouchListener: RosterDragDropAction? = null
    var itemTouchHelper:ItemTouchHelper? = null
    // Click Listeners
    private var itemClickListener: ((View, PlayerRef) -> Unit)? = onClickReturnViewT()
    var updateCallback: ((String, String) -> Unit)? = onClickReturnStringString()
    // Master List
    var playerRefList: RealmList<PlayerRef>? = null
    var playerFilters = mutableMapOf<String,String>()
    // Layout Details
    var recyclerView: RecyclerView? = null
    var layout: Int = R.layout.card_player_medium_grid
    var type: String = FireTypes.PLAYERS
    var size: String = "medium_grid"
    // Roster Details
    var selectedCount: Int = 20
    var rosterId: String? = null
    var isOpen: Boolean = false

    constructor(rosterId: String, recyclerView: RecyclerView, itemClickListener: ((View, PlayerRef) -> Unit)?, size: String) : this() {
        this.rosterId = rosterId
        this.recyclerView = recyclerView
        this.itemClickListener = itemClickListener
        this.size = size
        this.layout = RouterViewHolder.getLayout(type, size)
        this.setupRosterList()
    }

    constructor(realmList: RealmList<PlayerRef>?, itemClickListener: ((View, PlayerRef) -> Unit)?, size: String, rosterId:String) : this() {
        this.playerRefList = realmList
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
        return playerRefList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RouterViewHolder, position: Int) {
        println("binding roster player: $position")
        playerRefList?.let { itPlayerList ->
            itPlayerList[position]?.let { it1 ->

                if (position <= selectedCount) {
                    holder.itemView.setBackgroundColor(getColor(holder.itemView.context, R.color.ludiRosterCardSelected))
                } else {
                    holder.itemView.setBackgroundColor(getColor(holder.itemView.context, R.color.white))
                }

                holder.bind(it1 as RealmObject, position=position)
                holder.itemView.onLongClick {
                    it?.wiggleOnce()
                }
                holder.itemView.setOnClickListener {
                    itemClickListener?.invoke(it, it1)
                }
            }
        }
    }

    /** Load Roster by ID */
    fun loadRosterById(localRosterId: String?=null) {
        realmInstance.findRosterById(localRosterId ?: rosterId)?.let { roster ->
            playerRefList = roster.players?.ludiFilters(playerFilters)?.sortByOrderIndex()
        }
    }

    /** Setup Functions */
    private fun setupRosterList() {
        loadRosterById()
        addTouchAdapters()
        attach()
        notifyDataSetChanged()
    }
    private fun addTouchAdapters() {
        itemTouchListener = RosterDragDropAction(this)
        itemTouchHelper = ItemTouchHelper(itemTouchListener!!)
        itemTouchHelper?.attachToRecyclerView(recyclerView)
    }
    private fun attach() {
        recyclerView?.layoutManager = GridLayoutManager(recyclerView?.context, 2)
        recyclerView?.adapter = this
    }

    /** Disable Functions */
    fun disableAndClearRosterList() {
        itemTouchHelper?.attachToRecyclerView(null)
        itemTouchListener = null
        itemTouchHelper = null
        this.recyclerView?.adapter = null
    }

    /** Filter Functions */
    fun filterByStatusSelected() {
        this.playerFilters = ludiFilters("status" to PLAYER_STATUS_SELECTED)
        loadRosterById()
        notifyDataSetChanged()
    }

    /** Sort Functions */
    fun sortByOrderIndex() {
        playerRefList?.let { list ->
            // Perform updates in a Realm transaction
            realmInstance.safeWrite { _ ->
                val templist = list.sortByOrderIndex()
                list.clear()
                list.addAll(templist)
            }
        }
        notifyDataSetChanged()
    }

    /** Update Functions */
    fun updateOrderIndexes() {
        playerRefList?.let { list ->
            // Perform updates in a Realm transaction
            realmInstance.safeWrite { _ ->
                list.forEachIndexed { index, playerRef ->
                    playerRef.orderIndex = index
                }
            }
        }
    }

}




fun View.hideCompletely() {
    layoutParams.width = 0
    layoutParams.height = 0
}


