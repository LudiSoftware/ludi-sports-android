package io.usys.report.ui.ludi.roster

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import io.usys.report.realm.*
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


enum class RosterType(val type: String) {
    OFFICIAL("official"),
    OFFICIAL_ARCHIVE("official_archive"),
    TRYOUT("tryout"),
    SELECTED("selected"),
    UNSELECTED("unselected")
}

class RosterConfig(var teamId: String) {

    // Roster ID
    var rosterIds = mutableMapOf<String,String>()
    var rosterId: String? = null
    var tryoutRosterId: String? = null
    var currentRosterId: String? = null
    val realmInstance: Realm = realm()
    var recyclerView: RecyclerView? = null
    var parentFragment: Fragment? = null
    var playerViewModelLayout = null
    // Filters
    var filters = ludiFilters()
    var rosterSizeLimit: Int = 20
    // Mandatory
    var itemTouchHelper: ItemTouchHelper? = null
    // Optional
    var selectionCounter = 0
    var selectedItemColors = mutableMapOf<String, Int>() // <playerId, color resource>
    // Drag and Drop
    var itemTouchListener: RosterLiveDragDropAction? = null
    var itemLiveTouchListener: RosterLiveDragDropAction? = null
    // Update TextViews
    var textViewOne: TextView? = null

    init {
        realmInstance.findTryOutByTeamId(teamId) { tryout ->
            tryout.rosterId?.let {
                this.tryoutRosterId = it
                rosterIds[RosterType.TRYOUT.type] = it
            }
        }
        realmInstance.teamSessionByTeamId(teamId) { teamSession ->
            this.rosterId = teamSession.rosterId
            this.currentRosterId = teamSession.rosterId
            rosterIds[RosterType.OFFICIAL.type] = teamSession.rosterId ?: "unknown"
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

    fun switchRosterTo(rosterTypeStr:String) {
        val tt = RosterType.values().find { it.type == rosterTypeStr }
        val t = RosterType.valueOf(tt.toString()).type
        currentRosterId = rosterIds[t]
    }

    fun switchRosterTo(rosterType: RosterType) {
        currentRosterId = rosterIds[rosterType.type]
    }

    /** Helpers **/
    fun updateTextViewOne(text: String) {
        textViewOne?.text = text
    }
    fun setBuilderSubText() {
        if (selectedTooMany()) {
            textViewOne?.text = "Roster has too many players. ($selectionCounter)/($rosterSizeLimit)"
            return
        }
        else if (selectedNotEnough()) {
            textViewOne?.text = "Roster is short (${rosterSizeLimit-selectionCounter}) players."
            return
        }
        else if (selectedIsEqual()) {
            textViewOne?.text = "Roster is Ready to Submit!"
            return
        }
    }

    fun selectedTooMany() : Boolean {
        realmInstance.rosterSessionById(currentRosterId)?.let { rs ->
            return rs.playersSelectedCount > rs.rosterSizeLimit
        }
        return false
    }

    fun selectedNotEnough() : Boolean {
        realmInstance.rosterSessionById(currentRosterId)?.let { rs ->
            return rs.playersSelectedCount < rs.rosterSizeLimit
        }
        return false
    }

    fun selectedIsEqual() : Boolean {
        realmInstance.rosterSessionById(currentRosterId)?.let { rs ->
            return rs.playersSelectedCount == rs.rosterSizeLimit
        }
        return false
    }
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


