package io.usys.report.ui.ludi.formationbuilder

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.realm.local.teamSessionByTeamId
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.realm
import io.usys.report.realm.safeAdd
import io.usys.report.realm.safeWrite
import io.usys.report.ui.views.listAdapters.removeItemViewFromList
import io.usys.report.utils.bind
import io.usys.report.utils.inflateLayout
import io.usys.report.utils.views.*
import java.util.*

/**
 * RecyclerView Adapter
 */
class RosterFormationListAdapter() : RecyclerView.Adapter<RosterFormationListAdapter.RosterFormationViewHolder>(), ItemTouchHelperAdapter {
    var teamId: String? = null
    var realmInstance: Realm? = null
    var formationSessionId: String? = null
    var onDeckPlayerIdList: RealmList<String> = RealmList()
    var onItemMoved: ((start: Int, end: Int) -> Unit)? = null
    var activity: Activity? = null
    var filters: MutableMap<String, String>? = null

    constructor(teamId: String, realmInstance: Realm?, activity: Activity) : this() {
        this.teamId = teamId
        this.realmInstance = realmInstance ?: realm()
        this.onDeckPlayerIdList = this.loadFormationSessionData()
        this.activity = activity
    }

    constructor(teamId: String, realmInstance: Realm?, activity: Activity, filters:MutableMap<String,String>) : this() {
        this.teamId = teamId
        this.realmInstance = realmInstance ?: realm()
        this.filters = filters
        this.onDeckPlayerIdList = this.loadFormationSessionData()
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterFormationViewHolder {
        val view = parent.inflateLayout(R.layout.card_player_tiny_horizontal)
        return RosterFormationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterFormationViewHolder, position: Int) {
        val playerId = onDeckPlayerIdList[position] ?: "unknown"
        realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            // Filtering System
            fs.roster?.players?.find { it.id == playerId }?.let { player ->
                if (player.isFiltered("foot", "right")) {
                    holder.imageView.removeItemViewFromList()
                    return@teamSessionByTeamId
                }
                //Normal Display Setup
                holder.textView.text = player.name
                player.imgUrl?.let { itImgUrl ->
                    holder.imageView.loadUriIntoImgView(itImgUrl)
                }
                holder.itemView.setPlayerTeamBackgroundColor(player.color)

                // On Click
                holder.itemView.setOnClickListener {
                    activity?.let { it1 ->
                        //todo: use profile fragment now.
                    }
                }
                // On Long Click
                holder.itemView.setOnLongClickListener {
                    holder.itemView.wiggleOnce()
                    holder.startClipDataDragAndDrop(playerId)
                }
            }

        }
    }

    fun resetDeckToRoster() {
        this.onDeckPlayerIdList.clear()
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.formationSessionId = fs.id
            this.realmInstance?.safeWrite {
                fs.deckListIds?.clear()
                fs.formationListIds?.clear()
            }
            fs.roster?.players?.let { rosterPlayers ->
                for (playerId in rosterPlayers) {
                    this.addPlayer(playerId)
                }
            }
        }
        this.notifyDataSetChanged()
    }

    /** 1. Load in Roster **/
    private fun loadFormationSessionData() : RealmList<String> {
        val tempList: RealmList<String> = RealmList()
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.formationSessionId = fs.id
            fs.deckListIds?.let { deckList ->
                for (playerId in deckList) {
                    if (tempList.contains(playerId)) { continue }
                    tempList.safeAdd(playerId)
                }
            }
        }
        return tempList
    }

    private fun loadFormationSessionDataWithFilters() : RealmList<String> {
        val tempList: RealmList<String> = RealmList()
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.formationSessionId = fs.id
            fs.deckListIds?.let { deckList ->
                for (playerId in deckList) {
                    if (tempList.contains(playerId)) { continue }
                    tempList.safeAdd(playerId)
                }
            }
        }
        return tempList
    }
    /** HELPER: Move Player  **/
    fun movePlayerToField(playerId: String) {
        onDeckPlayerIdList.indexOf(playerId).let { itIndex ->
            this.realmInstance?.safeWrite {
                it.teamSessionByTeamId(teamId!!) { fs ->
                    fs.deckListIds?.remove(playerId)
                    fs.formationListIds?.safeAdd(playerId)
                }
            }
            onDeckPlayerIdList.remove(playerId)
            notifyItemRemoved(itIndex)
        }
    }
    fun movePlayerToDeck(playerId: String) {
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.realmInstance?.safeWrite {
                fs.deckListIds?.safeAdd(playerId)
                fs.formationListIds?.remove(playerId)
            }
        }
        onDeckPlayerIdList.add(playerId)
        notifyItemInserted(onDeckPlayerIdList.size - 1)
    }
    fun removePlayer(playerId: String) {
        onDeckPlayerIdList.indexOf(playerId).let { itIndex ->
            this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
                this.realmInstance?.safeWrite {
                    fs.deckListIds?.remove(playerId)
                    fs.blackListIds?.safeAdd(playerId)
                }
            }
            onDeckPlayerIdList.remove(playerId)
            notifyItemRemoved(itIndex)
        }
    }

    fun addPlayer(player: PlayerRef) {
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.realmInstance?.safeWrite {
                fs.deckListIds?.safeAdd(player.id)
            }
        }
        onDeckPlayerIdList.add(player.id)
//        notifyItemInserted(onDeckPlayerIdList.size - 1)
    }

    override fun getItemCount() = onDeckPlayerIdList.size

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        // This is for movements within the recyclerView. Not for dragging to the soccer field.
        return true
    }

    override fun onItemDismiss(position: Int) {
        onDeckPlayerIdList.removeAt(position)
        notifyItemRemoved(position)
    }

    /** ViewHolder **/
    inner class RosterFormationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.bind(R.id.cardPlayerTinyHorizontalTxtName)
        val imageView: ImageView = itemView.bind(R.id.cardPlayerTinyHorizontalImgProfile)
    }
}

fun String?.matchesFilter(filterOption:String): Boolean {
    if (this == null) return false
    if (this.equals(filterOption, ignoreCase = true)) {
        return true
    }
    return false
}

fun isFilteredOutPlayer(player:PlayerRef, filters: Map<String, String>): Boolean {
    for (filter in filters) {
        when (filter.key.toLowerCase(Locale.getDefault())) {
            "status" -> {
                if (player.status.equals(filter.value, ignoreCase = true)) {
                    return true
                }
            }
            "position" -> {
                if (player.position.equals(filter.value, ignoreCase = true)) {
                    return true
                }
            }
            "foot" -> {
                if (player.foot.equals(filter.value, ignoreCase = true)) {
                    return true
                }
            }
            // Add more attributes to filter here
        }
    }
    return false
}

fun PlayerRef.isFiltered(filterKey:String, filterValue:String): Boolean {
    when (filterKey.toLowerCase(Locale.getDefault())) {
        "status" -> {
            if (this.status.equals(filterValue, ignoreCase = true)) {
                return true
            }
        }
        "position" -> {
            if (this.position.equals(filterValue, ignoreCase = true)) {
                return true
            }
        }
        "foot" -> {
            if (this.foot.equals(filterValue, ignoreCase = true)) {
                return true
            }
        }
        // Add more attributes to filter here
    }
    return false
}
fun RealmList<PlayerRef>.filterByStatus(statusFilter: String): RealmList<PlayerRef> {
    val filteredList = RealmList<PlayerRef>()
    for (player in this) {
        if (player.status.equals(statusFilter, ignoreCase = true)) {
            filteredList.add(player)
        }
    }
    return filteredList
}

fun getStatusFilter(statusFilter: String): MutableMap<String, String> {
    return mutableMapOf("status" to statusFilter)
}

fun getFootFilter(footFilter: String): MutableMap<String, String> {
    return mutableMapOf("foot" to footFilter)
}

fun RealmList<PlayerRef>.filterByAttribute(key:String, value:String): RealmList<PlayerRef> {
    val attributes = mapOf(key to value)
    return this.filterByAttributes(attributes)
}

fun RealmList<PlayerRef>.filterByAttributes(attributes: Map<String, String>): RealmList<PlayerRef> {
    val filteredList = RealmList<PlayerRef>()

    for (player in this) {
        var matchesAllAttributes = true

        for ((attribute, value) in attributes) {
            when (attribute.toLowerCase(Locale.getDefault())) {
                "status" -> {
                    if (player.status?.toLowerCase(Locale.getDefault()) != value.toLowerCase(Locale.getDefault())) {
                        matchesAllAttributes = false
                    }
                }
                "position" -> {
                    if (player.position?.toLowerCase(Locale.getDefault()) != value.toLowerCase(
                            Locale.getDefault())) {
                        matchesAllAttributes = false
                    }
                }
                "foot" -> {
                    if (player.foot?.toLowerCase(Locale.getDefault()) != value.toLowerCase(Locale.getDefault())) {
                        matchesAllAttributes = false
                    }
                }
                // Add more attributes to filter here
            }
            if (!matchesAllAttributes) break
        }

        if (matchesAllAttributes) {
            filteredList.add(player)
        }
    }

    return filteredList
}