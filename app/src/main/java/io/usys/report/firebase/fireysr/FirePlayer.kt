package io.usys.report.firebase

import io.realm.RealmList
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.addObjectToSessionList

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
                val teamObject = ds?.toObject<Team>()
                addObjectToSessionList(teamObject)
            }
    }
}