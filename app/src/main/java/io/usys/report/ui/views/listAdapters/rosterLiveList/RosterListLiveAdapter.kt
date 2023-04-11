package io.usys.report.ui.views.listAdapters.rosterLiveList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import io.realm.*
import io.usys.report.realm.*
import io.usys.report.realm.local.RosterSession
import io.usys.report.realm.local.rosterSessionById
import io.usys.report.realm.local.setupRosterSession
import io.usys.report.realm.model.PLAYER_STATUS_SELECTED
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.ui.ludi.player.ludiFilters
import io.usys.report.ui.ludi.player.sortByOrderIndex
import io.usys.report.ui.ludi.roster.RosterConfig
import io.usys.report.ui.ludi.roster.RosterType
import io.usys.report.ui.views.recyclerViews.LudiBaseListAdapter
import io.usys.report.ui.views.touchAdapters.RosterLiveDragDropAction
import io.usys.report.utils.log



/**
 * Dynamic Master RecyclerView Adapter
 */
open class RosterListLiveAdapter(): LudiBaseListAdapter<Roster, PlayerRef, RosterPlayerViewHolder>() {

    var rosterId:String? = null
    var mode: String? = null
    var layout: Int = 0
    var touchEnabled: Boolean = false
    lateinit var config: RosterConfig
    init { realmInstance.isAutoRefresh = true }

    constructor(teamId: String) : this() {
        this.config = RosterConfig(teamId)
        this.init()
    }
    constructor(rosterLayoutConfig: RosterConfig) : this() {
        this.config = rosterLayoutConfig
        (config.currentRosterId)?.let { realmInstance.setupRosterSession(it) }
        this.init()
    }

    private fun observeRosterPlayers() {
        realmInstance.observe<RosterSession>(config.parentFragment!!.viewLifecycleOwner) { results ->
            results.find { it.id == config.currentRosterId }?.let {
                log("Roster Session Live Updates")
                this.reload()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterPlayerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RosterPlayerViewHolder(itemView)
    }

    override fun onBind(holder: RosterPlayerViewHolder, position: Int) {
        println("binding realmlist")
        itemList?.let {
            it[position]?.let { it1 ->
                if (mode == RosterType.TRYOUT.type) {
                    val result = holder.bindTryout(it1, adapter=this, counter=config.selectionCounter)
                    if (result) config.selectionCounter++
                } else if (mode == RosterType.SELECTED.type) {
                    holder.bindSelection(it1, position=position, rosterLimit=config.rosterSizeLimit)
                } else {
                    holder.bind(it1, position=position)
                }
            }
        }
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
        if (mode == RosterType.TRYOUT.type || mode == RosterType.SELECTED.type) getPlayersSelectedCount()
        if (touchEnabled) addTouchAdapters()
        attach()
        observeRosterPlayers()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setup() {
        disableAndClearRosterList()
        loadRosterById()
        realmInstance.rosterSessionById(config.currentRosterId) {
            this.mode = it.mode
            this.layout = it.layout
            this.touchEnabled = it.touchEnabled
        }
        if (mode == RosterType.TRYOUT.type || mode == RosterType.SELECTED.type) getPlayersSelectedCount()
        if (touchEnabled) addTouchAdapters()
        attach()
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun reload() {
        setRosterSizeLimit()
        loadRosterById()
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        config.selectionCounter = 0
        setRosterSizeLimit()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun softRefresh() {
        config.selectionCounter = 0
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
        config.currentRosterId = config.tryoutRosterId
        config.clearFilters()
        mode = RosterType.TRYOUT.type
        touchEnabled = true
        reload()
    }

    fun setupSelectionRoster() {
        config.currentRosterId = config.tryoutRosterId
        mode = RosterType.SELECTED.type
        touchEnabled = true
        setFilterStatusSelection()
        reload()
    }

    /** Touch Functions */
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
        this.itemList?.clear()
    }

    /** Filter Functions */

    private fun setFilterStatusSelection() {
        this.config.filters = ludiFilters("status" to PLAYER_STATUS_SELECTED)
    }
    fun filterByStatusSelected() {
        this.config.filters = ludiFilters("status" to PLAYER_STATUS_SELECTED)
        this.itemList = this.itemList?.ludiFilters(this.config.filters)
        notifyDataSetChanged()
    }

    /** Add Functions */
    private fun getPlayersSelectedCount() {
        realmInstance.rosterSessionById(rosterId ?: config.rosterId)?.let { rs ->
            realmInstance.findRosterById(rosterId ?: config.rosterId)?.let { roster ->
                realmInstance.safeWrite {
                    rs.playersSelectedCount = roster.players?.ludiFilters(ludiFilters("status" to PLAYER_STATUS_SELECTED))?.count() ?: 0
                }
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
    fun areTooManySelected() : Boolean {
        realmInstance.rosterSessionById(rosterId ?: config.rosterId)?.let { rs ->
            return rs.playersSelectedCount > rs.rosterSizeLimit
        }
        return false
    }

    private fun getRosterSize() : Int {
        realmInstance.rosterSessionById(rosterId ?: config.rosterId)?.let { rs ->
            return rs.rosterSizeLimit
        }
        return 20
    }

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

    fun setRosterSizeLimit() {
        realmInstance.rosterSessionById(config.currentRosterId) { rosterSession ->
            this.config.rosterSizeLimit = rosterSession.rosterSizeLimit
        }
    }

}



