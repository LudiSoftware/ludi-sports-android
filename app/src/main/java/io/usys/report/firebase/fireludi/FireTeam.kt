package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.realm.Realm
import io.realm.RealmList
import io.usys.report.realm.model.*
import io.usys.report.utils.log

/** Teams Profiles */

fun Realm.fireGetTeamProfileInBackground(teamId:String) {
    firebaseDatabase {
        it.child(FireTypes.TEAMS).child(teamId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toLudiObject<Team>(this)
                log("Team Updated")
            }
    }
}

fun Realm.fireGetTeamProfilesInBackground(teamIds: RealmList<String>) {
    teamIds.forEach { teamId ->
        firebaseDatabase {
            it.child(FireTypes.TEAMS).child(teamId)
                .fairAddListenerForSingleValueEvent { ds ->
                    ds?.toLudiObject<Team>(this)
                    log("Team Updated: $teamId")
                }
        }
    }
}

fun Realm.fireGetTeamProfilesInBackground(teamIds: MutableList<String>) {
    teamIds.forEach { teamId ->
        firebaseDatabase {
            it.child(FireTypes.TEAMS).child(teamId)
                .fairAddListenerForSingleValueEvent { ds ->
                    ds?.toLudiObject<Team>(this)
                    log("Team Updated: $teamId")
                }
        }
    }
}

class TeamFireListener: ValueEventListener {
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        dataSnapshot.toLudiObject<Team>()
        log("Roster Updated")
    }

    override fun onCancelled(databaseError: DatabaseError) {
        log("Error: ${databaseError.message}")
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
                ds?.toLudiObjects<TryOut>()
                log("TryOut Updated")
            }
    }
}

