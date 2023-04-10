package io.usys.report.ui.views.listAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.realm.*
import io.usys.report.R
import io.usys.report.realm.*
import io.usys.report.realm.model.PLAYER_STATUS_SELECTED
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.ui.ludi.player.ludiFilters
import io.usys.report.ui.ludi.player.sortByOrderIndex
import io.usys.report.ui.ludi.roster.RosterConfig
import io.usys.report.ui.ludi.roster.RosterPlayerViewHolder
import io.usys.report.ui.ludi.roster.RosterType
import io.usys.report.ui.views.touchAdapters.RosterDragDropAction
import io.usys.report.ui.views.touchAdapters.RosterLiveDragDropAction
import io.usys.report.utils.log

/**
 * Dynamic Master RecyclerView Adapter
 */
open class RosterListLiveAdapter(): RecyclerView.Adapter<RosterPlayerViewHolder>() {

    var realmInstance = realm()
    var rosterId:String? = null
    var results: RealmResults<Roster>? = null
    var playerList: RealmList<PlayerRef>? = RealmList()
    var config: RosterConfig = RosterConfig()

    init {
        realmInstance.isAutoRefresh = true
    }

    constructor(rosterLayoutConfig: RosterConfig) : this() {
        this.config = rosterLayoutConfig
        this.setup()
    }

    private fun observeRosterPlayers() {
        results = realmInstance.observeRoster(config.parentFragment!!.viewLifecycleOwner) { results ->
            results.find { it.id == config.rosterId }?.let {
                log("Roster Live Updates")
                loadRosterById()
            }
        }
    }

    private fun destroyObserver() {
        realmInstance.removeAllChangeListeners()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterPlayerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(config.layout, parent, false)
        return RosterPlayerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return playerList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RosterPlayerViewHolder, position: Int) {
        println("binding realmlist")
        playerList?.let {
            it[position]?.let { it1 ->
                if (config.mode == RosterType.TRYOUT.type) {
                    val result = holder.bindTryout(it1, adapter=this, counter=config.selectionCounter)
                    if (result) config.selectionCounter++
                } else if (config.mode == RosterType.SELECTED.type) {
                    holder.bindSelection(it1, position=position, rosterLimit=config.rosterSizeLimit)
                } else {
                    holder.bind(it1, position=position)
                }
            }
        }
    }

    /** Load Roster by ID */
    private fun loadRosterById() {
        realmInstance.findRosterById(config.rosterId)?.let { roster ->
            this.playerList = roster.players?.ludiFilters(config.playerFilters)?.sortByOrderIndex()
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setup() {
        destroyObserver()
        observeRosterPlayers()
        if (config.mode == RosterType.TRYOUT.type || config.mode == RosterType.SELECTED.type) getPlayersSelectedCount()
        if (config.touchEnabled) addTouchAdapters()
        attach()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        config.selectionCounter = 0
        notifyDataSetChanged()
    }

    private fun addTouchAdapters() {
        config.itemLiveTouchListener = RosterLiveDragDropAction(this)
        config.itemTouchHelper = ItemTouchHelper(config.itemLiveTouchListener!!)
        config.itemTouchHelper?.attachToRecyclerView(config.recyclerView)
    }
    private fun attach() {
        config.recyclerView?.layoutManager = GridLayoutManager(config.recyclerView?.context, 2)
        config.recyclerView?.adapter = this
    }

    /** Disable Functions */
    fun disableAndClearRosterList() {
        config.destroy()
        config = RosterConfig()
        this.playerList?.clear()
    }

    /** Filter Functions */
    fun filterByStatusSelected() {
        this.config.playerFilters = ludiFilters("status" to PLAYER_STATUS_SELECTED)
        this.playerList = this.playerList?.ludiFilters(this.config.playerFilters)
        notifyDataSetChanged()
    }

    /** Add Functions */
    private fun getPlayersSelectedCount() {
        realmInstance.findRosterById(config.rosterId)?.let { roster ->
            config.playersSelectedCount = roster.players?.ludiFilters(ludiFilters("status" to PLAYER_STATUS_SELECTED))?.count() ?: 0
        }
    }

    /** Update Functions */
    fun updateOrderIndexes() {
        playerList?.let { list ->
            // Perform updates in a Realm transaction
            realmInstance.safeWrite { _ ->
                list.forEachIndexed { index, playerRef ->
                    playerRef.orderIndex = index + 1
                }
            }
        }
    }

    /** Helper Functions */
    fun areTooManySelected() : Boolean {
        return config.playersSelectedCount > config.rosterSizeLimit
    }

}



