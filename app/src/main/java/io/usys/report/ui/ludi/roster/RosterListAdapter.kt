package io.usys.report.ui.ludi.roster

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.findRosterById
import io.usys.report.realm.local.teamSessionByTeamId
import io.usys.report.realm.model.PLAYER_STATUS_SELECTED
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.ui.ludi.player.*
import io.usys.report.ui.views.touchAdapters.*


/**
 * Custom Roster Setups
 */
fun LudiRosterRecyclerView?.setupRosterGridArrangable(id: String) {
    val roster = realm().findRosterById(id)
    val players: RealmList<PlayerRef> = roster?.players?.sortByOrderIndex() ?: RealmList()
    players.let {
        val config = RosterConfig()
        config.rosterId = id
        val adapter = RosterListAdapter(config)
        // Drag and Drop
        val itemTouchListener = RosterDragDropAction(adapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchListener)
        // Extra
//        val touchListener = RosterItemTouchListener(viewPager2)
        //Attachments
        itemTouchHelper.attachToRecyclerView(this)
        // RecyclerView
//        this?.addOnItemTouchListener(touchListener)
        this?.layoutManager = GridLayoutManager(this!!.context, 2)
        this.adapter = adapter

    }
}

enum class TeamStatus(val status: String) {
    IN_SEASON("open"),
    POST_SEASON("pending"),
    TRYOUT("finalized"),
    PENDING("archive"),
    ARCHIVE("archive"),
    DEAD("dead")
}

enum class PlayerStatus(val status: String) {
    OPEN("open"),
    PENDING("pending"),
    SELECTED("finalized"),
    SEND_LETTER("archive"),
    PENDING_APPROVAL("dead"),
    APPROVED("approved"),
    REJECTED("rejected"),
}

enum class RosterStatus(val status: String) {
    OPEN("open"),
    PENDING("pending"),
    FINALIZED("finalized"),
    ARCHIVE("archive"),
    DEAD("dead")
}


enum class RosterType(val type: String) {
    OFFICIAL("official"),
    OFFICIAL_ARCHIVE("official_archive"),
    TRYOUT("tryout"),
    SELECTED("selected"),
    UNSELECTED("unselected")
}

class RosterConfig {
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
    var rosterSizeLimit: Int = 20
    var playerFilters = ludiFilters()
    var playersSelectedCount: Int = 0
    var selectionCounter = 0
    // Click Listeners
    var touchEnabled: Boolean = true
    var itemTouchListener: RosterDragDropAction? = null
    var itemTouchHelper: ItemTouchHelper? = null

    fun setRosterSizeLimit(realmInstance: Realm?, teamId: String?){
        realmInstance?.teamSessionByTeamId(teamId) { teamSession ->
            this.rosterSizeLimit = teamSession.rosterSizeLimit
        }
    }

    fun updateRosterSizeLimit(realmInstance: Realm?, teamId: String?, newSizeLimit: Int){
        this.rosterSizeLimit = newSizeLimit
        realmInstance?.teamSessionByTeamId(teamId) { teamSession ->
            realmInstance.safeWrite {
                teamSession.rosterSizeLimit = newSizeLimit
            }
        }
    }

    fun destroy() {
        itemTouchHelper?.attachToRecyclerView(null)
        recyclerView = null
        itemTouchListener = null
        itemTouchHelper = null
        playerFilters.clear()
    }

}

/**
 * Dynamic Master RecyclerView Adapter
 */
open class RosterListAdapter(): RecyclerView.Adapter<RosterPlayerViewHolder>() {

    // Realm
    var realmInstance = realm()
    var config: RosterConfig = RosterConfig()
    // Master List
    var playerRefList: RealmList<PlayerRef>? = null
    var counter = 0

    constructor(rosterLayoutConfig: RosterConfig) : this() {
        this.config = rosterLayoutConfig
        this.setup()
    }
    constructor(rosterId: String) : this() {
        this.config = RosterConfig().apply { this.rosterId = rosterId }
        this.setup()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterPlayerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(config.layout, parent, false)
        return RosterPlayerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return playerRefList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RosterPlayerViewHolder, position: Int) {
        println("binding roster player: $position")
        playerRefList?.let { itPlayerList ->
            itPlayerList[position]?.let { it1 ->
                if (config.mode == RosterType.TRYOUT.type) {
                    val result = holder.bindTryout(it1, adapter=this, counter=counter)
                    if (result) counter++
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
            playerRefList = roster.players?.ludiFilters(config.playerFilters)?.sortByOrderIndex()
        }
    }

    /** Setup Functions */
    @SuppressLint("NotifyDataSetChanged")
    private fun setup() {
        loadRosterById()
        if (config.mode == RosterType.TRYOUT.type || config.mode == RosterType.SELECTED.type) getPlayersSelectedCount()
        if (config.touchEnabled) addTouchAdapters()
        attach()
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun reload() {
        disableAndClearRosterList()
        setup()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        counter = 0
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
        config.destroy()
        config = RosterConfig()
        this.playerRefList?.clear()
    }

    /** Filter Functions */
    fun filterByStatusSelected() {
        this.config.playerFilters = ludiFilters("status" to PLAYER_STATUS_SELECTED)
        loadRosterById()
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
        playerRefList?.let { list ->
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


