package io.usys.report.firebase.fireludi

import io.realm.Realm
import io.usys.report.firebase.*
import io.usys.report.providers.syncTeamDataFromFirebase
import io.usys.report.realm.model.*
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

/**
 * TryOuts
 */
inline fun Realm.fireGetTryOutById(tryoutId:String?, crossinline callback: (TryOut?) -> Unit) {
    if (tryoutId == null) return
    firebaseDatabase {
        it.child(DatabasePaths.TRYOUTS.path).child(tryoutId)
            .singleValueEvent { ds ->
                val tempResults = ds?.toLudiObject<TryOut>(this)
                callback(tempResults)
                log("TryOut Updated")
            }
    }
}

