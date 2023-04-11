package io.usys.report.ui.ludi.roster

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.*
import io.usys.report.realm.local.RosterSession
import io.usys.report.realm.local.rosterSessionById
import io.usys.report.realm.local.teamSessionByTeamId
import io.usys.report.realm.model.PlayerRef
import io.usys.report.ui.fragments.toPlayerProfile
import io.usys.report.ui.ludi.player.*
import io.usys.report.ui.views.listAdapters.rosterLiveList.RosterListLiveAdapter
import io.usys.report.ui.views.touchAdapters.*


/**
 * Custom Roster Setups
 */
fun LudiRosterRecyclerView?.setupRosterGridArrangable(id: String) {
    val roster = realm().findRosterById(id)
    val players: RealmList<PlayerRef> = roster?.players?.sortByOrderIndex() ?: RealmList()
    players.let {
        val config = RosterConfig(teamId = roster?.teamId!!)
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



class RosterConfig(var teamId: String) {

    // Roster ID
    var rosterId: String? = null
    var tryoutRosterId: String? = null
    var currentRosterId: String? = null
    val realmInstance: Realm = realm()
    var recyclerView: RecyclerView? = null
    var parentFragment: Fragment? = null
    // Filters
    var filters = ludiFilters()
    var rosterSizeLimit: Int = 20
    // Mandatory
    var itemTouchHelper: ItemTouchHelper? = null
    // Optional
    var selectionCounter = 0
    // Drag and Drop
    var itemTouchListener: RosterLiveDragDropAction? = null
    var itemLiveTouchListener: RosterLiveDragDropAction? = null

    init {
        realmInstance.findTryOutByTeamId(teamId) { tryout ->
            tryout.rosterId?.let {
                this.tryoutRosterId = it
            }
        }
        realmInstance.teamSessionByTeamId(teamId) { teamSession ->
            this.rosterId = teamSession.rosterId
            this.currentRosterId = teamSession.rosterId
        }

        rosterId?.let {
            currentRosterId = it
            realmInstance.rosterSessionById(it) { rosterSession ->
                this.rosterSizeLimit = rosterSession.rosterSizeLimit
                realmInstance.safeWrite {
                    rosterSession.teamId = teamId
                    rosterSession.tryoutRosterId = this.tryoutRosterId
                }
            }
        }
    }

    /** Helpers **/
    fun switchToOfficialRoster() {
        rosterId?.let { currentRosterId = it }
    }
    fun switchToTryoutRoster() {
        tryoutRosterId?.let { currentRosterId = it }
    }
    fun clearFilters() {
        filters.clear()
    }
    fun toPlayerProfile(id: String?) {
        if (id == null) return
        this.parentFragment?.toPlayerProfile(playerId = id)
    }
    fun destroy() {
        itemTouchHelper?.attachToRecyclerView(null)
        itemTouchHelper = null
        filters.clear()
    }

}


