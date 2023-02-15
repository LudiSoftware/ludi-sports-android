package io.usys.report.firebase

import io.realm.RealmList
import io.usys.report.model.addObjectToSessionList
import io.usys.report.model.toTeamObject

fun fireGetPlayerProfiles(playerIds: RealmList<String>) {
    for (id in playerIds) {
        fireGetTeamProfileForSession(id)
    }
}
fun fireGetPlayerProfilesForSession(teamId:String) {
    firebaseDatabase {
        it.child(FireTypes.TEAMS).child(teamId)
            .fairAddListenerForSingleValueEvent { ds ->
                //TODO:
                val teamObject = ds?.toHashMapWithRealmLists().toTeamObject()
                addObjectToSessionList(teamObject)
            }
    }
}