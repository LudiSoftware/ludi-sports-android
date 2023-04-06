package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.usys.report.realm.model.*
import io.usys.report.utils.log

/**
 * Get Roster
 */
fun fireGetRosterInBackground(rosterId:String) {
    firebaseDatabase {
        it.child(DatabasePaths.ROSTERS.path).orderByChild("id").equalTo(rosterId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toLudiObjects<Roster>()
                log("Roster Updated")
            }
    }
}
/**
 * Update Roster
 */

class RosterFireListener: ValueEventListener {
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        dataSnapshot.toLudiObjects<Roster>()
        log("Roster Updated")
    }

    override fun onCancelled(databaseError: DatabaseError) {
        log("Error: ${databaseError.message}")
    }
}