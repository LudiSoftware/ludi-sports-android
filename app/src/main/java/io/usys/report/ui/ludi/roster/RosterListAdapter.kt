package io.usys.report.ui.ludi.roster

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.findRosterById
import io.usys.report.realm.model.PLAYER_STATUS_SELECTED
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.TEAM_MODE_TRYOUT
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.ui.ludi.player.*
import io.usys.report.ui.onClickReturnViewT
import io.usys.report.ui.views.touchAdapters.*


/**
 * Custom Roster Setups
 */
fun LudiRosterRecyclerView?.setupRosterGridArrangable(id: String, onPlayerClick: ((View, PlayerRef) -> Unit)?, viewPager2: ViewPager2) {
    val roster = realm().findRosterById(id)
    val players: RealmList<PlayerRef> = roster?.players?.sortByOrderIndex() ?: RealmList()
    players.let {
        val config = RosterLayoutConfig()
        config.rosterId = id
        config.itemClickListener = onPlayerClick
        val adapter = RosterListAdapter(config)
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

class RosterLayoutConfig {
    // Roster ID
    var rosterId: String? = null
    var recyclerView: RecyclerView? = null
    // Mandatory
    var layout: Int = R.layout.card_player_medium_grid
    var type: String = FireTypes.PLAYERS
    var size: String = "medium_grid"
    var mode: String = "official"
    var isOpen: Boolean = true
    // Optional
    var selectedCount: Int = 20
    var playerFilters = ludiFilters()
    // Click Listeners
    var touchEnabled: Boolean = true
    var itemClickListener: ((View, PlayerRef) -> Unit)? = onClickReturnViewT()
    var itemTouchListener: RosterDragDropAction? = null
    var itemTouchHelper: ItemTouchHelper? = null

}

/**
 * Dynamic Master RecyclerView Adapter
 */
open class RosterListAdapter(): RecyclerView.Adapter<RosterPlayerViewHolder>() {

    // Realm
    var realmInstance = realm()
    var config: RosterLayoutConfig = RosterLayoutConfig()
    // Master List
    var playerRefList: RealmList<PlayerRef>? = null

    constructor(rosterLayoutConfig: RosterLayoutConfig) : this() {
        this.config = rosterLayoutConfig
        this.setup()
    }
    constructor(rosterId: String) : this() {
        this.config = RosterLayoutConfig().apply { this.rosterId = rosterId }
        this.setup()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterPlayerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(config.layout, parent, false)
        return RosterPlayerViewHolder(itemView, config)
    }

    override fun getItemCount(): Int {
        return playerRefList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RosterPlayerViewHolder, position: Int) {
        println("binding roster player: $position")
        playerRefList?.let { itPlayerList ->
            itPlayerList[position]?.let { it1 ->
                if (config.mode == TEAM_MODE_TRYOUT) {
                    holder.bindTryout(it1, position=position, selectedCount=config.selectedCount)
                } else {
                    holder.bind(it1, position=position)
                }
            }

        }
    }

    /** Load Roster by ID */
    private fun loadRosterById() {
        realmInstance.findRosterById(config.rosterId)?.let { roster ->
            playerRefList = roster.players?.ludiFilters(config.playerFilters)?.sortByOrderIndex()
        }
    }

    /** Setup Functions */
    @SuppressLint("NotifyDataSetChanged")
    private fun setup() {
        loadRosterById()
        if (config.touchEnabled) addTouchAdapters()
        attach()
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun reload() {
        disableAndClearRosterList()
        loadRosterById()
        if (config.touchEnabled) addTouchAdapters()
        attach()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        disableAndClearRosterList()
        loadRosterById()
        if (config.touchEnabled) addTouchAdapters()
        attach()
        notifyDataSetChanged()
    }
    private fun addTouchAdapters() {
        config.itemTouchListener = RosterDragDropAction(this)
        config.itemTouchHelper = ItemTouchHelper(config.itemTouchListener!!)
        config.itemTouchHelper?.attachToRecyclerView(config.recyclerView)
    }
    private fun attach() {
        config.recyclerView?.layoutManager = GridLayoutManager(config.recyclerView?.context, 2)
        config.recyclerView?.adapter = this
    }

    /** Disable Functions */
    fun disableAndClearRosterList() {
        config = RosterLayoutConfig()
        this.config.recyclerView?.adapter = null
        this.playerRefList?.clear()
        this.config.playerFilters.clear()
    }


    /** Filter Functions */
    fun filterByStatusSelected() {
        this.config.playerFilters = ludiFilters("status" to PLAYER_STATUS_SELECTED)
        loadRosterById()
        notifyDataSetChanged()
    }


    /** Update Functions */
    fun updateOrderIndexes() {
        playerRefList?.let { list ->
            // Perform updates in a Realm transaction
            realmInstance.safeWrite { _ ->
                list.forEachIndexed { index, playerRef ->
                    playerRef.orderIndex = index + 1
                }
            }
        }
    }

}




fun View.hideCompletely() {
    layoutParams.width = 0
    layoutParams.height = 0
}


