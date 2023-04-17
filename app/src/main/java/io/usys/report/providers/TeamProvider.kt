package io.usys.report.providers

import io.realm.Realm
import io.usys.report.firebase.*
import io.usys.report.firebase.fireludi.fireGetTeamById
import io.usys.report.firebase.fireludi.fireGetTryOutById
import io.usys.report.realm.*
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.TryOut
import io.usys.report.utils.log

/** Master
 *  -> Full Team Pull
 **/
fun Realm.syncTeamDataFromFirebase(teamId: String?, depth: Int = 0) {
    if (teamId == null) return
    if (depth > 5) {
        log("Firebase Team Retries Exceeded")
        return
    }
    val team = this.findTeamById(teamId)
    if (team == null) {
        log("Team $teamId is null")
        this.fireGetTeamById(teamId) {
            if (it != null) this.syncTeamDataFromFirebase(teamId, depth + 1)
        }
    } else {
        // Official Team Roster
        this.ifRosterDoesNotExist(team.rosterId) { rosterId ->
            fireGetRosterInBackground(rosterId, this)
        }
        // Tryout + Roster
        this.ifObjectDoesNotExist<TryOut>(team.tryoutId) {itTryoutId ->
            this.fireGetTryOutById(itTryoutId) {
                this.ifRosterDoesNotExist(it?.rosterId) { rosterId ->
                    fireGetRosterInBackground(rosterId, this)
                }
            }
        }
    }
}

fun Realm.syncTeamDataFromFirebaseForce(teamId: String?) {
    if (teamId == null) return
    this.fireGetTeamById(teamId) { itTeam ->
        itTeam?.rosterId?.let { fireGetRosterInBackground(it, this) }
        this.fireGetTryOutById(itTeam?.tryoutId) { itTryout ->
            itTryout?.rosterId?.let { fireGetRosterInBackground(it, this) }
        }
    }
}

/** Update Team Mode **/
fun Realm.fireUpdateTeamMode(teamId: String?) {
    teamId?.let { itTeamId ->
        this.findTeamById(teamId)?.let { itTeam ->
            firebaseDatabase { itDB ->
                itDB.child("teams")
                    .child(itTeamId)
                    .child("mode")
                    .setValue(itTeam.mode)
            }
        }
    }
}

/** Helpers **/
inline fun Realm.ifRosterDoesNotExist(rosterId: String?, crossinline block: (String) -> Unit?) {
    if (rosterId == null) return
    this.ifObjectDoesNotExist<Roster>(rosterId) { block(rosterId) }
}


