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
fun Realm.fireGetTryOutProfileIntoRealm(teamId:String?) {
    if (teamId == null) return
    firebaseDatabase {
        it.child(DatabasePaths.TRYOUTS.path).orderByChild("teamId").equalTo(teamId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toRealmObjects<TryOut>()
                log("TryOut Updated")
            }
    }
}

