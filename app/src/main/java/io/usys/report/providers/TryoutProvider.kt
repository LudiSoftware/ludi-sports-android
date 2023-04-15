package io.usys.report.providers
import io.realm.Realm
import io.usys.report.firebase.firebaseDatabase
import io.usys.report.realm.*

fun Realm.fireUpdateTryoutMode(teamId: String?) {
    teamId?.let { itTeamId ->
        this.findTryOutByTeamId(itTeamId) { tryout ->
            firebaseDatabase { itDB ->
                itDB.child("tryouts")
                    .child(tryout.id)
                    .child("mode")
                    .setValue(tryout.mode)
            }
        }
    }
}

fun Realm.fireUpdateRosterStatus(rosterId: String?) {
    rosterId?.let { itRosterId ->
        this.findRosterById(itRosterId)?.let { roster ->
            firebaseDatabase { itDB ->
                itDB.child("rosters")
                    .child(itRosterId)
                    .child("status")
                    .setValue(roster.status)
            }
        }
    }
}

/** 1. Registration */
fun Realm.tryoutChangeModeToRegistration(teamId:String) {
    val team = this.findTeamById(teamId)
    val tryout = this.findTryOutById(team?.tryoutId)
    val roster = this.findRosterById(tryout?.rosterId)
    this.safeWrite {
        team?.mode = TeamMode.TRYOUT.mode
        tryout?.mode = TryoutMode.REGISTRATION.mode
        roster?.status = RosterMode.REGISTRATION.mode
    }
    this.refresh()
}

/** 2. Tryout */
fun Realm.tryoutChangeModeToTryout(teamId:String) {
    val team = this.findTeamById(teamId)
    val tryout = this.findTryOutById(team?.tryoutId)
    val roster = this.findRosterById(tryout?.rosterId)
    this.safeWrite {
        team?.mode = TeamMode.TRYOUT.mode
        tryout?.mode = TryoutMode.TRYOUT.mode
        roster?.status = RosterMode.TRYOUT.mode
    }
    this.refresh()
}

/** 3. Pending Roster */
fun Realm.tryoutChangeModeToPendingRoster(teamId:String) {
    val team = this.findTeamById(teamId)
    val tryout = this.findTryOutById(team?.tryoutId)
    val roster = this.findRosterById(tryout?.rosterId)
    this.safeWrite {
        team?.mode = TeamMode.PENDING_ROSTER.mode
        tryout?.mode = TryoutMode.PENDING_ROSTER.mode
        roster?.status = RosterMode.PENDING_ROSTER.mode
    }
    this.refresh()
}

/** 4. Complete
 * TODO: Change Team Roster to new Roster ID
 * TODO: Change Old Roster to Archive
 * */
fun Realm.tryoutChangeModeToComplete(teamId:String) {
    val team = this.findTeamById(teamId)
    val tryout = this.findTryOutById(team?.tryoutId)
    val roster = this.findRosterById(tryout?.rosterId)
    this.safeWrite {
        team?.mode = TeamMode.OFF_SEASON.mode
        tryout?.mode = TryoutMode.COMPLETE.mode
        roster?.status = RosterMode.COMPLETE.mode
    }
    this.refresh()
}