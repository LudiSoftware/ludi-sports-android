package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.realm.Realm
import io.usys.report.realm.findPlayersInRosterById
import io.usys.report.realm.model.*
import io.usys.report.utils.log

/**
 * Get Roster
 */
fun fireGetRosterInBackground(rosterId:String, realm: Realm?=null) {
    firebaseDatabase {
        it.child(DatabasePaths.ROSTERS.path).child(rosterId)
            .singleValueEvent { ds ->
                ds?.toLudiObject<Roster>(realm)
                log("Roster $rosterId Pulled")
            }
    }
}
/**
 * Update Roster
 */

fun Realm.fireUpdatePlayersInRoster(rosterId: String) {
    this.findPlayersInRosterById(rosterId)?.let { players ->
        val firePlayers = realmListToDataList(players)
        firebaseDatabase {
            it.child(DatabasePaths.ROSTERS.path)
                .child(rosterId)
                .child("players")
                .setValue(firePlayers)
        }
    }
}

class RosterFireListener: ValueEventListener {
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        dataSnapshot.toLudiObjects<Roster>()
        log("Roster Updated")
    }

    override fun onCancelled(databaseError: DatabaseError) {
        log("Error: ${databaseError.message}")
    }
}