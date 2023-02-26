package io.usys.report.firebase

import io.realm.RealmList
import io.usys.report.realm.model.Coach
import io.usys.report.realm.model.Session.Companion.addCoachToSession
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TryOut
import io.usys.report.realm.model.addObjectToSessionList
import io.usys.report.realm.toRealmList
import io.usys.report.realm.writeToRealm
import io.usys.report.utils.log

/** Teams Profiles */
fun fireGetTeamsProfiles(teamIds: RealmList<String>) {
    for (id in teamIds) {
        fireGetTeamProfileForSession(id)
    }
}
fun fireGetTeamProfileForSession(teamId:String) {
    firebaseDatabase {
        it.child(FireTypes.TEAMS).child(teamId)
            .fairAddListenerForSingleValueEvent { ds ->
                val teamObject = ds?.toRealmObjectCast<Team>()
                addObjectToSessionList(teamObject)
                log("Team Updated")
            }
    }
}
inline fun fireGetTeamProfile(teamId:String, crossinline block: (Team?) -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.TEAMS).child(teamId)
            .fairAddListenerForSingleValueEvent { ds ->
                val teamObject = ds?.toRealmObjectCast<Team>()
                block(teamObject as? Team)
            }
    }
}
/**
 * TryOuts
 */

fun fireGetTryOutProfileForSession(teamId:String) {
    firebaseDatabase {
        it.child(FireTypes.TRYOUTS).orderByChild("teamId").equalTo(teamId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toRealmObjects<TryOut>()
                log("Team Updated")
            }
    }
}