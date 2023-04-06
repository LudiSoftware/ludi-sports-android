package io.usys.report.ui.ludi.roster

import com.google.firebase.database.DatabaseReference
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.usys.report.firebase.*
import io.usys.report.firebase.models.convertForFirebase
import io.usys.report.realm.findRosterById
import io.usys.report.realm.findRosterIdByTeamId
import io.usys.report.realm.model.Roster
import io.usys.report.realm.realm
import io.usys.report.utils.log

class RosterProvider {

    val realmInstance = realm()
    var rosterId: String? = null
    var teamId: String? = null
    var rosterIds = mutableListOf<String>()
    var updateCallBack: ((rosterId:String) -> Unit)? = null
    var rosterReference: DatabaseReference? = null
    var rosterFireListener: RosterFireListener
    var syncRosterFireListener: RosterFireListener
    var syncRosterRealmListener: RosterRealmListener<Roster>? = null

    constructor(rosterId: String) {
        this.rosterId = rosterId
    }

    init {
        teamId?.let {
            rosterId = realmInstance.findRosterIdByTeamId(it)
        }
        firebaseDatabase {
            rosterReference = it.child(DatabasePaths.ROSTERS.path)
        }
        rosterFireListener = RosterFireListener()
        syncRosterFireListener = RosterFireListener()
    }

    /** FireRoster */
    // PULL
    fun pullRosterFromFirebase() {
        rosterId?.let {
            fireGetRosterInBackground(it)
        } ?: run {
            rosterIds.forEach { rosterId ->
                fireGetRosterInBackground(rosterId)
            }
        }
    }
    // PUSH
    fun pushRosterToFirebase() {
        rosterId?.let { itRosterId ->
            realmInstance.findRosterById(itRosterId)?.let { itRoster ->
                val data = itRoster.convertForFirebase()
                firebaseDatabase { itDB ->
                    itDB.child("rosters")
                        .child(itRosterId)
                        .setValue(data)
                }
            }
        }
    }

    // SUBSCRIBE
    fun removeRealmRosterListener() {
        realmInstance.removeAllChangeListeners()
    }
    fun subscribeToFirebase() {
        rosterReference?.child(DatabasePaths.ROSTERS.path)
            ?.orderByChild("id")
            ?.equalTo(rosterId)
            ?.addValueEventListener(rosterFireListener)
    }
    fun removeFireRosterListener() {
        rosterReference?.removeEventListener(rosterFireListener)
    }

    /** FirePlayers */
    fun pushPlayersToFirebase() {
        rosterId?.let { itRosterId ->
            realmInstance.findRosterById(itRosterId)?.let { itRoster ->
                itRoster.players?.let { itPlayers ->
                    val playersDataList = realmListToDataList(itPlayers)
                    firebaseDatabase { itDB ->
                        itDB.child("rosters")
                            .child(itRosterId)
                            .child("players")
                            .setValue(playersDataList)
                    }
                }
            }
        }
    }
}
class RosterRealmListener<T>(val rosterId: String, private val onRealmChange: () -> Unit) : RealmChangeListener<T> {
    override fun onChange(t: T) {
        log("Roster listener called")
        val r = t as? Roster
        if (rosterId != r?.id) return
        onRealmChange()
    }
}


inline fun Realm.subscribeToRosterUpdates(rosterId: String? = null, crossinline updateCallBack: (String) -> Unit) {
    val rosterListener = RealmChangeListener<Roster> { roster ->
        // Handle changes to the Realm data here
        log("Roster listener called")
        roster.id?.let { id -> updateCallBack(id) }
    }
    this.where(Roster::class.java)?.equalTo("id", rosterId)?.findFirstAsync()?.addChangeListener(rosterListener)
}
