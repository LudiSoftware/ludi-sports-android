package io.usys.report.providers
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.realm.Realm
import io.usys.report.firebase.firebaseDatabase
import io.usys.report.firebase.models.convertForFirebase
import io.usys.report.firebase.realmListToDataList
import io.usys.report.firebase.toLudiObject
import io.usys.report.realm.*
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TryOut
import io.usys.report.utils.log


class RosterFireListener(val realm: Realm): ValueEventListener {
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        dataSnapshot.toLudiObject<Roster>(realm)
        log("Roster Updated")
    }

    override fun onCancelled(databaseError: DatabaseError) {
        log("Error: ${databaseError.message}")
    }
}


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