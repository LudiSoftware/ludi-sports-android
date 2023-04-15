package io.usys.report.providers

import io.realm.Realm
import io.usys.report.firebase.*
import io.usys.report.firebase.models.convertForFirebase
import io.usys.report.realm.*
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.TryOut
import io.usys.report.utils.log

/** PUSH ROSTERS **/
fun Realm.pushRosterToFirebase(rosterId: String?) {
    rosterId?.let { itRosterId ->
        this.findRosterById(itRosterId)?.let { itRoster ->
            val data = itRoster.convertForFirebase()
            firebaseDatabase { itDB ->
                itDB.child("rosters")
                    .child(itRosterId)
                    .setValue(data)
            }
        }
    }
}

/** Push Players to Firebase Roster **/
fun Realm.pushPlayersToRosterInFirebase(rosterId: String) {
    this.findRosterById(rosterId)?.let { itRoster ->
        itRoster.players?.let {
            val data = realmListToDataList(it)
            firebaseDatabase { itDB ->
                itDB.child("rosters")
                    .child(rosterId)
                    .child("players")
                    .setValue(data)
            }
        }
    }
}

/** Pull Team Details from Firebase **/
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

inline fun Realm.ifRosterDoesNotExist(rosterId: String?, crossinline block: (String) -> Unit?) {
    if (rosterId == null) return
    this.ifObjectDoesNotExist<Roster>(rosterId) { block(rosterId) }
}