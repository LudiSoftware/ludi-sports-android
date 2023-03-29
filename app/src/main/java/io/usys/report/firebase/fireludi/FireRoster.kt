package io.usys.report.firebase

import io.usys.report.realm.model.*
import io.usys.report.realm.toHashMap
import io.usys.report.utils.log

/**
 * Get Roster
 */
fun fireGetRosterInBackground(rosterId:String) {
    firebaseDatabase {
        it.child(DatabasePaths.ROSTERS.path).orderByChild("id").equalTo(rosterId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toRealmObjects<Roster>()
                log("Roster Updated")
            }
    }
}
/**
 * Update Roster
 */
fun Roster.fireUpdateRoster() {
    this.id?.let {
        val tosend = this.toHashMap()
        fireAddUpdateDBAsync(DatabasePaths.ROSTERS.path, it, tosend)
        log("Roster Updated")
    }
}