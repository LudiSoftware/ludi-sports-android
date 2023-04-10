package io.usys.report.ui.ludi.roster

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.findRosterById
import io.usys.report.realm.local.teamSessionByTeamId
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.ui.fragments.toPlayerProfile
import io.usys.report.ui.ludi.player.*
import io.usys.report.ui.views.listAdapters.RosterListLiveAdapter
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
        val adapter = RosterListLiveAdapter(config)
        // Drag and Drop
        val itemTouchListener = RosterLiveDragDropAction(adapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchListener)
        //Attachments
        itemTouchHelper.attachToRecyclerView(this)
        // RecyclerView
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

open class ListConfig {
    var recyclerView: RecyclerView? = null
    var parentFragment: Fragment? = null
    // Filters
    var filters = ludiFilters()
    // Mandatory
    var layout: Int = R.layout.card_player_medium_grid
    var type: String = FireTypes.PLAYERS
    var size: String = "medium_grid"
    var mode: String = "official"
    var isOpen: Boolean = true
    // Click Listeners
    var touchEnabled: Boolean = true
    var itemTouchHelper: ItemTouchHelper? = null

    fun toPlayerProfile(id: String?) {
        if (id == null) return
        this.parentFragment?.toPlayerProfile(playerId = id)
    }

    fun destroy() {
        itemTouchHelper?.attachToRecyclerView(null)
        recyclerView = null
        itemTouchHelper = null
        filters.clear()
    }
}

class RosterConfig: ListConfig() {
    // Roster ID
    var rosterId: String? = null
    // Optional
    var rosterSizeLimit: Int = 20
    var playersSelectedCount: Int = 0
    var selectionCounter = 0
    // Drag and Drop
    var itemTouchListener: RosterLiveDragDropAction? = null
    var itemLiveTouchListener: RosterLiveDragDropAction? = null

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

}


