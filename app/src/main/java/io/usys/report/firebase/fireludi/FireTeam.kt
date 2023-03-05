package io.usys.report.firebase

import io.realm.Realm
import io.usys.report.realm.model.*
import io.usys.report.utils.log

/** Teams Profiles */
fun Realm.fireGetTeamProfileInBackground(teamId:String) {
    firebaseDatabase {
        it.child(FireTypes.TEAMS).child(teamId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toRealmObjectCast<Team>(this)
                log("Team Updated")
            }
    }
}

/**
 * TryOuts
 */
fun fireGetTryOutProfileIntoRealm(teamId:String?) {
    if (teamId == null) return
    firebaseDatabase {
        it.child(DatabasePaths.TRYOUTS.path).orderByChild("teamId").equalTo(teamId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toRealmObjects<TryOut>()
                log("Team Updated")
            }
    }
}

/**
 * Roster
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