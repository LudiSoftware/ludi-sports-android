package io.usys.report.realm.local

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.findRosterById
import io.usys.report.realm.ifObjectDoesNotExist
import io.usys.report.realm.safeAdd
import io.usys.report.realm.safeWrite
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable


open class RosterSession : RealmObject(), Serializable {
    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String = getTimeStamp()
    var teamId: String? = "null"
    var rosterId: String? = "null"
    var tryoutRosterId: String? = "null"
    var isTryout: Boolean = false
    var rosterSizeLimit: Int = 20
    var playersSelectedCount: Int = 0
    var playerIds: RealmList<String>? = RealmList()
    var rosterSplits: Int = 0
    //formation
    var teamColorsAreOn: Boolean = true
    var currentLayout: Int = 0
    var layoutList: RealmList<Int>? = RealmList()
    var formationListIds: RealmList<String>? = RealmList()
    var deckListIds: RealmList<String>? = RealmList()
    // Layout
    var layout: Int = R.layout.roster_player_card_grid_medium
    var type: String = FireTypes.PLAYERS
    var size: String = "medium_grid"
    var mode: String = "official"
    var isOpen: Boolean = true
    // Click Listeners
    var touchEnabled: Boolean = true
}

inline fun Realm.rosterSessionById(rosterId:String?, crossinline block: (RosterSession) -> Unit) {
    this.where(RosterSession::class.java).equalTo("id", rosterId).findFirst()?.let {
        block(it)
    }
}

fun Realm.rosterSessionById(rosterId:String?): RosterSession? {
    if (rosterId.isNullOrEmpty()) return null
    return this.where(RosterSession::class.java).equalTo("id", rosterId).findFirst()
}

fun Realm.rosterSessionSizeLimit(rosterId:String?, default:Int=20): Int {
    val result = this.where(RosterSession::class.java).equalTo("id", rosterId).findFirst()
    return result?.rosterSizeLimit ?: default
}

fun Realm.updateRosterSizeLimit(rosterId: String?, newSizeLimit: Int){
    this.rosterSessionById(rosterId) { rosterSession ->
        this.safeWrite {
            rosterSession.rosterSizeLimit = newSizeLimit
        }
    }
}

fun Realm.setupRosterSession(rosterId:String) {
    this.ifObjectDoesNotExist<RosterSession>(rosterId) {
        this.safeWrite { itRealm ->
            val itRosterSession = itRealm.createObject(RosterSession::class.java, rosterId)
            itRosterSession?.currentLayout = R.drawable.soccer_field
            itRosterSession?.layoutList = RealmList(R.drawable.soccer_field)
            itRosterSession?.playerIds = RealmList()
            itRosterSession?.deckListIds = RealmList()
            itRosterSession?.formationListIds = RealmList()
            this.findRosterById(rosterId)?.let { itRoster ->
                itRosterSession?.teamId = itRoster.teamId
                itRoster.players?.let { rosterPlayers ->
                    for (player in rosterPlayers) {
                        itRosterSession?.playerIds?.safeAdd(player.id)
                        itRosterSession?.deckListIds?.safeAdd(player.id)
                    }
                }
            }
            itRosterSession?.let {
                itRealm.insertOrUpdate(it)
            }
        }
    }
}
