package io.usys.report.ui.ludi.roster.config

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.usys.report.realm.*
import io.usys.report.realm.local.rosterSessionById
import io.usys.report.realm.local.teamSessionByTeamId
import io.usys.report.ui.ludi.player.*
import io.usys.report.ui.views.navController.toPlayerProfile
import io.usys.report.ui.views.touchAdapters.*
import io.usys.report.utils.ludi.HeaderViewScrollListener
import io.usys.report.utils.ludi.PlayerID
import io.usys.report.utils.ludi.RosterID
import io.usys.report.utils.ludi.RosterTypeName

enum class RosterType(val type: String) {
    OFFICIAL("official"),
    OFFICIAL_ARCHIVE("official_archive"),
    TRYOUT("tryout"),
    SELECTED("selected"),
    UNSELECTED("unselected"),
    CUSTOM("custom")
}

open class RosterListeners {
    var itemTouchHelper: ItemTouchHelper? = null
    // Drag and Drop
    var itemTouchListener: RosterLiveDragDropAction? = null
    var itemLiveTouchListener: RosterLiveDragDropAction? = null
    var headerViewScrollListener: HeaderViewScrollListener? = null
}

open class RosterViews {
    var recyclerView: RecyclerView? = null
    var headerView: View? = null
    var playerViewModelLayout: View? = null
    var textViewOne: TextView? = null

    fun addRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }
    fun addHeaderView(headerView: View) {
        this.headerView = headerView
    }
    fun addPlayerViewModelLayout(playerViewModelLayout: View) {
        this.playerViewModelLayout = playerViewModelLayout
    }
    fun addTextViewOne(textViewOne: TextView) {
        this.textViewOne = textViewOne
    }
}

class RosterTryout() {
    // Optional
    var selectionCounter = 0
    var selectedItemColors = mutableMapOf<String, Int>() // <playerId, color resource>
}

open class RosterController(var teamId: String) : RosterViews() {

    var rosterListeners = RosterListeners()
    // Roster ID
    var rosterIds = mutableMapOf<String,String>()
    var rosterId: String? = null
    var tryoutRosterId: String? = null
    var currentRosterId: String? = null
    val realmInstance: Realm = realm()
    var parentFragment: Fragment? = null
    // Filters
    var filters = ludiFilters()
    var rosterSizeLimit: Int = 20
    // Optional
    var selectionCounter = 0
    var selectedItemColors = mutableMapOf<String, Int>() // <playerId, color resource>

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

    fun switchRosterTo(rosterTypeStr:RosterTypeName) {
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
    fun toPlayerProfile(id:PlayerID?, rosterID:RosterID) {
        if (id == null) return
        this.parentFragment?.toPlayerProfile(playerId = id, rosterId = rosterID)
    }
    fun destroy() {
        rosterListeners.itemTouchHelper?.attachToRecyclerView(null)
        rosterListeners.itemTouchHelper = null
        filters.clear()
    }

    // Configurations
    fun addHeaderScrollListener(headerV: View?=null) {
        recyclerView?.addOnScrollListener(HeaderViewScrollListener(headerV ?: headerView ?: return))
    }

}


