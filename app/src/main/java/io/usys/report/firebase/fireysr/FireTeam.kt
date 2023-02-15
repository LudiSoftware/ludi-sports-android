package io.usys.report.firebase

import io.realm.RealmList
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.addObjectToSessionList
import io.usys.report.realm.model.toTeamObject

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
                val teamObject = ds?.toHashMapWithRealmLists().toTeamObject()
                addObjectToSessionList(teamObject)
            }
    }
}
inline fun fireGetTeamProfile(teamId:String, crossinline block: (Team?) -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.TEAMS).child(teamId)
            .fairAddListenerForSingleValueEvent { ds ->
                val teamObject = ds?.toHashMapWithRealmLists().toTeamObject()
                block(teamObject)
            }
    }
}
