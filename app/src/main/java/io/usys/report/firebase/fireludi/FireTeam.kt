package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.realm.Realm
import io.realm.RealmList
import io.usys.report.providers.syncTeamDataFromFirebase
import io.usys.report.realm.model.*
import io.usys.report.realm.realm
import io.usys.report.utils.log

/** Teams Profiles */

fun Realm.fireGetTeamProfileInBackground(teamId:String) {
    firebaseDatabase {
        it.child(FireTypes.TEAMS).child(teamId)
            .singleValueEvent { ds ->
                ds?.toLudiObject<Team>(this)
                this.syncTeamDataFromFirebase(teamId)
                log("Fire Team $teamId Pulled")
            }
    }
}

inline fun Realm.fireGetTeamById(teamId:String, crossinline callback: (Team?) -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.TEAMS).child(teamId)
            .singleValueEvent { ds ->
                val team = ds?.toLudiObject<Team>(this)
                callback(team)
                log("Fire Team $teamId Pulled")
            }
    }
}

fun Realm.fireGetTeamProfilesInBackground(teamIds: RealmList<String>) {
    teamIds.forEach { teamId ->
        firebaseDatabase {
            it.child(FireTypes.TEAMS).child(teamId)
                .singleValueEvent { ds ->
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
                .singleValueEvent { ds ->
                    ds?.toLudiObject<Team>(this)
                    log("Team Updated: $teamId")
                }
        }
    }
}

class TeamFireListener: ValueEventListener {
    val realm = realm()
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        dataSnapshot.toLudiObject<Team>(realm)
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
            .singleValueEvent { ds ->
                val tempResults = ds?.toLudiObjects<TryOut>()
                if (tempResults != null) {
                    tempResults.first()?.let { itTO ->
                        itTO.rosterId?.let { rosterId ->
                            fireGetRosterInBackground(rosterId)
                        }
                    }
                }
                log("TryOut Updated")
            }
    }
}

inline fun Realm.fireGetTryOutById(tryoutId:String?, crossinline callback: (TryOut?) -> Unit) {
    if (tryoutId == null) return
    firebaseDatabase {
        it.child(DatabasePaths.TRYOUTS.path).child(tryoutId)
            .singleValueEvent { ds ->
                val tempResults = ds?.toLudiObject<TryOut>()
                callback(tempResults)
                log("TryOut Updated")
            }
    }
}

fun Realm.fireGetTryOutProfileById(tryoutId:String?) {
    if (tryoutId == null) return
    firebaseDatabase {
        it.child(DatabasePaths.TRYOUTS.path).child(tryoutId)
            .singleValueEvent { ds ->
                ds?.toLudiObject<TryOut>(this)
                log("TryOut Updated")
            }
    }
}

