package io.usys.report.ui.views.listAdapters.rosterLiveList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import io.realm.*
import io.usys.report.realm.*
import io.usys.report.realm.local.rosterSessionById
import io.usys.report.realm.local.setupRosterSession
import io.usys.report.realm.model.PLAYER_STATUS_SELECTED
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.ui.ludi.player.ludiFilters
import io.usys.report.ui.ludi.player.sortByOrderIndex
import io.usys.report.ui.ludi.roster.RosterConfig
import io.usys.report.ui.ludi.roster.RosterType
import io.usys.report.ui.views.listAdapters.LudiBaseListAdapter
import io.usys.report.ui.views.touchAdapters.RosterLiveDragDropAction
import io.usys.report.utils.log



/**
 * Dynamic Master RecyclerView Adapter
 */
open class RosterListLiveAdapter(): LudiBaseListAdapter<Roster, PlayerRef, RosterPlayerViewHolder>() {

    lateinit var config: RosterConfig

    constructor(rosterLayoutConfig: RosterConfig) : this() {
        this.config = rosterLayoutConfig
        this.layout = io.usys.report.R.layout.roster_player_card_grid_medium
        (config.currentRosterId)?.let { realmInstance.setupRosterSession(it) }
        this.init()
    }

    /** Observe Roster Changes and Update List */
    override fun observeRealmIds() {
        realmInstance.observe<Roster>(config.parentFragment!!.viewLifecycleOwner) { results ->
            results.find { it.id == config.currentRosterId }?.let {
                log("Roster Session Live Updates")
                this.setPlayersSelectedCount()
                this.softReload()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterPlayerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(io.usys.report.R.layout.roster_player_card_grid_medium, parent, false)
        return RosterPlayerViewHolder(itemView, this)
    }

    override fun onBind(holder: RosterPlayerViewHolder, position: Int) {
        println("binding realmlist")
        itemList?.let {
            it[position]?.let { it1 ->
                if (mode == RosterType.TRYOUT.type) {
                    holder.bindTryout(it1)
                } else if (mode == RosterType.SELECTED.type) {
                    holder.bindSelection(it1, position=position)
                } else {
                    holder.bind(it1, position=position)
                }
            }
        }
    }


    override fun onBindFinished() {
        super.onBindFinished()
        config.setBuilderSubText()
    }

    /** Load Roster by ID */
    private fun loadRosterById() {
        realmInstance.findRosterById(config.currentRosterId)?.let { roster ->
            this.itemList?.clear()
            this.itemList = roster.players?.ludiFilters(config.filters)?.sortByOrderIndex()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init() {
        loadRosterById()
        setRosterSizeLimit()
        realmInstance.rosterSessionById(config.currentRosterId) {
            this.mode = it.mode
            this.layout = it.layout
            this.touchEnabled = it.touchEnabled
        }
        setPlayersSelectedCount()
        addTouchAdapters()
        attach()
        observeRealmIds()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun reload() {
        setRosterSizeLimit()
        setPlayersSelectedCount()
        loadRosterById()
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun softReload() {
        loadRosterById()
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        config.selectionCounter = 0
        config.selectedItemColors.clear()
        setRosterSizeLimit()
        setPlayersSelectedCount()
        notifyDataSetChanged()
        config.setBuilderSubText()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun softRefresh() {
        config.selectionCounter = 0
        config.selectedItemColors.clear()
        notifyDataSetChanged()
    }

    /** Pre-Sets */
    fun setupOfficialRoster() {
        config.currentRosterId = config.rosterId
        config.clearFilters()
        mode = RosterType.OFFICIAL.type
        touchEnabled = false
        reload()
    }
    fun setupTryoutRoster() {
        config.clearFilters()
        config.currentRosterId = config.tryoutRosterId
        mode = RosterType.TRYOUT.type
        touchEnabled = true
        reload()
    }
    fun setupSelectionRoster() {
        config.clearFilters()
        config.currentRosterId = config.tryoutRosterId
        mode = RosterType.SELECTED.type
        touchEnabled = true
        setFilterStatusSelection()
        reload()
    }

    /** Touch Functions */
    private fun addTouchAdapters() {
        if (!touchEnabled) return
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
        config.selectedItemColors.clear()
        this.itemList?.clear()
    }

    /** Filter Functions */

    private fun setFilterStatusSelection() {
        this.config.filters = ludiFilters("status" to PLAYER_STATUS_SELECTED)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun filterByStatusSelected() {
        this.config.filters = ludiFilters("status" to PLAYER_STATUS_SELECTED)
        this.itemList = this.itemList?.ludiFilters(this.config.filters)
        notifyDataSetChanged()
    }

    /** Players Selected */
    private fun setPlayersSelectedCount() {
        realmInstance.rosterSessionById(config.currentRosterId)?.let { rs ->
            realmInstance.safeWrite {
                rs.playersSelectedCount = itemList?.ludiFilters(ludiFilters("status" to PLAYER_STATUS_SELECTED))?.count() ?: 0
            }
        }
    }

    /** Update Functions */
    fun updateOrderIndexes() {
        itemList?.let { list ->
            // Perform updates in a Realm transaction
            realmInstance.safeWrite { _ ->
                list.forEachIndexed { index, playerRef ->
                    playerRef.orderIndex = index + 1
                }
            }
        }
    }

    /** Helper Functions */
    @SuppressLint("NotifyDataSetChanged")
    fun updateRosterSizeLimit(newSizeLimit: Int) {
        realmInstance.rosterSessionById(config.currentRosterId) { rosterSession ->
            realmInstance.safeWrite {
                rosterSession.rosterSizeLimit = newSizeLimit
            }
        }
        this.config.rosterSizeLimit = newSizeLimit
        this.refresh()
    }

    private fun setRosterSizeLimit() {
        realmInstance.rosterSessionById(config.currentRosterId) { rosterSession ->
            this.config.rosterSizeLimit = rosterSession.rosterSizeLimit
        }
    }

}



