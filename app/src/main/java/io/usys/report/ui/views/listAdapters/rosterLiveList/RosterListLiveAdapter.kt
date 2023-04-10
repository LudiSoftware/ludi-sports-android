package io.usys.report.ui.views.listAdapters.rosterLiveList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import io.realm.*
import io.usys.report.realm.*
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterPlayerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(config.layout, parent, false)
        return RosterPlayerViewHolder(itemView)
    }

    override fun onBind(holder: RosterPlayerViewHolder, position: Int) {
        println("binding realmlist")
        itemList?.let {
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
            this.itemList = roster.players?.ludiFilters(config.filters)?.sortByOrderIndex()
            notifyDataSetChanged()
        }
    }

    protected fun attach() {
        config.recyclerView?.layoutManager = GridLayoutManager(config.recyclerView?.context, 2)
        config.recyclerView?.adapter = this
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

    /** Disable Functions */
    fun disableAndClearRosterList() {
        config.destroy()
        config = RosterConfig()
        this.itemList?.clear()
    }

    /** Filter Functions */
    fun filterByStatusSelected() {
        this.config.filters = ludiFilters("status" to PLAYER_STATUS_SELECTED)
        this.itemList = this.itemList?.ludiFilters(this.config.filters)
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
        return config.playersSelectedCount > config.rosterSizeLimit
    }

}



