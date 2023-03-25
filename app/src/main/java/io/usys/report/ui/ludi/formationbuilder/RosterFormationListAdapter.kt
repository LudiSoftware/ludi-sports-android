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
import io.usys.report.utils.bind
import io.usys.report.utils.inflateLayout
import io.usys.report.utils.makeGone
import io.usys.report.utils.views.*
import java.util.*

/**
 * RecyclerView Adapter
 *
 * 1. Original Full Roster of Players
 * 2. OnDeck Players List (Players Not Saved to the Formation Screen)
 * 3. Formation Players List (Players Currently Saved to the Formation Screen)
 * 4. Filtered Players List (Players WHO DO match filters BUT NOT in the Formation Screen)
 */
class RosterFormationListAdapter() : RecyclerView.Adapter<RosterFormationListAdapter.RosterFormationViewHolder>(), ItemTouchHelperAdapter {
    var teamId: String? = null
    var realmInstance: Realm? = null
    var formationSessionId: String? = null
    // 1. Original Full Roster of Players
    var fullRosterIds: RealmList<String> = RealmList()
    // 2. OnDeck Players List (Players Not Saved to the Formation Screen)
    var onDeckPlayerIdList: RealmList<String> = RealmList()
    // 3/4. Filtered Players List (Players WHO DO match filters BUT NOT in the Formation Screen)
    var filteredPlayerIds: RealmList<String> = RealmList()
    var filters: MutableMap<String, String>? = null
    //
    var onItemMoved: ((start: Int, end: Int) -> Unit)? = null
    var activity: Activity? = null
    var recyclerView: RecyclerView? = null



    constructor(teamId: String, realmInstance: Realm?, activity: Activity) : this() {
        this.teamId = teamId
        this.realmInstance = realmInstance ?: realm()
        this.onDeckPlayerIdList = this.loadFormationSessionOnDeckList()
        this.activity = activity
    }

    constructor(teamId: String, realmInstance: Realm?, activity: Activity, filters:MutableMap<String,String>) : this() {
        this.teamId = teamId
        this.realmInstance = realmInstance ?: realm()
        this.filters = filters
        this.onDeckPlayerIdList = this.loadFormationSessionOnDeckList()
        this.activity = activity
    }
    constructor(teamId: String, realmInstance: Realm?, activity: Activity, filters:MutableMap<String,String>, recyclerView: RecyclerView) : this() {
        this.teamId = teamId
        this.realmInstance = realmInstance ?: realm()
        this.filters = filters
        this.onDeckPlayerIdList = this.loadFormationSessionOnDeckList()
        this.activity = activity
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterFormationViewHolder {
        val view = parent.inflateLayout(R.layout.card_player_tiny_horizontal)
        return RosterFormationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterFormationViewHolder, position: Int) {
        val playerId = onDeckPlayerIdList[position] ?: "unknown"
        var isNotFiltered = true
        realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            // Filtering System
            fs.roster?.players?.find { it.id == playerId }?.let { player ->
                //Normal Display Setup
                holder.textView.text = player.foot
                player.imgUrl?.let { itImgUrl ->
                    holder.imageView.loadUriIntoImgView(itImgUrl)
                }
                holder.itemView.setPlayerTeamBackgroundColor(player.color)
                // On Click
                holder.itemView.setOnClickListener {
                    activity?.let { _ ->
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

    /** Reset OnDeck to Original Roster **/
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

    /** 1. Load in Roster
     *      - ALWAYS filter out players that are in the formation list.
     *      -
     * **/
    private fun loadFormationSessionOnDeckList() : RealmList<String> {
        val tempList: RealmList<String> = RealmList()
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.formationSessionId = fs.id
            fs.formationListIds?.let { this.filteredPlayerIds.addAll(it) }
            //load from full roster
            fs.roster?.players?.let { rosterPlayers ->
                for (player in rosterPlayers) {
                    if (this.filteredPlayerIds.contains(player.id)) { continue }
                    if (filters != null) {
                        if (player.isFiltered(filters!!)) {
                            this.filteredPlayerIds.safeAdd(player.id)
                            continue
                        }
                    }
                    tempList.safeAdd(player.id)
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
    fun addPlayer(player: PlayerRef) {
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.realmInstance?.safeWrite {
                fs.deckListIds?.safeAdd(player.id)
            }
        }
        onDeckPlayerIdList.add(player.id)
//        notifyItemInserted(onDeckPlayerIdList.size - 1)
    }

    fun hideItem(position: Int) {
        val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position) as? RosterFormationViewHolder
        viewHolder?.itemView?.visibility = View.GONE
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



fun PlayerRef.isFiltered(filters: MutableMap<String,String>): Boolean {
    for (filter in filters) {
        if (this.isFiltered(filter.key, filter.value)) {
            return true
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
