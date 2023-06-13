package io.usys.report.providers
import io.realm.Realm
import io.usys.report.firebase.firebaseDatabase
import io.usys.report.firebase.models.convertForFirebase
import io.usys.report.firebase.realmListToDataList
import io.usys.report.realm.*

fun Realm.fireUpdateRosterStatus(rosterId: String?) {
    rosterId?.let { itRosterId ->
        this.findRosterById(itRosterId)?.let { roster ->
            firebaseDatabase { itDB ->
                itDB.child("rosters")
                    .child(itRosterId)
                    .child("status")
                    .setValue(roster.status)
            }
        }
    }
}

/** PUSH ROSTERS **/
fun Realm.fireRosterUpdateRoster(rosterId: String?) {
    rosterId?.let { itRosterId ->
        this.findRosterById(itRosterId)?.let { itRoster ->
            val data = itRoster.convertForFirebase()
            firebaseDatabase { itDB ->
                itDB.child("rosters")
                    .child(itRosterId)
                    .setValue(data)
            }
        }
    }
}

/** Push Players to Firebase Roster **/
fun Realm.fireRosterUpdatePlayers(rosterId: String) {
    this.findRosterById(rosterId)?.let { itRoster ->
        itRoster.players?.let {
            val data = realmListToDataList(it)
            firebaseDatabase { itDB ->
                itDB.child("rosters")
                    .child(rosterId)
                    .child("players")
                    .setValue(data)
            }
        }
    }
}