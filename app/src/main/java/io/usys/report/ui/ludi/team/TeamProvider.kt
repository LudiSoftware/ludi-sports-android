package io.usys.report.ui.ludi.team

import com.google.firebase.database.DatabaseReference
import io.realm.Realm
import io.usys.report.firebase.*
import io.usys.report.firebase.models.convertForFirebase
import io.usys.report.realm.*
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TryOut
import io.usys.report.utils.log

class TeamProvider(val teamId: String) {

    val realmInstance = realm()
    var teamIsComplete: Boolean = false
    var officialRosterId: String? = null
    var officialIsComplete: Boolean = false
    var tryoutId: String? = null
    private var tryoutIsComplete: Boolean = false
    var tryoutRosterId: String? = null
    var tryoutRosterIsComplete: Boolean = false
    var teamReference: DatabaseReference? = null
    private var teamFireListener: TeamFireListener

    var rosterSubscriptions: MutableList<() -> Unit> = mutableListOf()

    init {
        pullTeamAndDetailsFromFirebase()
        firebaseDatabase {
            teamReference = it.child(DatabasePaths.TEAMS.path)
        }
        teamFireListener = TeamFireListener()
    }

    /** FireTeam
     * 1. Team
     * 2. OfficialRoster
     * 3. Tryout
     * 4. TryOutRoster
     * */
    fun pullTeamAndDetailsFromFirebase() {
        realmInstance.findTeamById(teamId)?.let { team ->
            teamIsComplete = true
            // Official Roster
            team.rosterId?.let { rosterId ->
                this.officialRosterId = rosterId
                if (!officialRosterIsComplete()) {
                    fireGetRosterInBackground(rosterId)
                }
            }
            // Tryout
            team.tryoutId?.let { tryoutId ->
                if (!tryoutIsComplete()) {
                    realmInstance.fireGetTryOutProfileById(tryoutId)
                } else {
                    realmInstance.findTryOutIdByTeamId(teamId)?.let {
                        this.tryoutRosterId = tryoutId
                        fireGetRosterInBackground(tryoutId)
                    }
                }
            }
        } ?: run {
            // Team
            realmInstance.ifObjectDoesNotExist<Team>(teamId) {
                realmInstance.fireGetTeamProfileInBackground(teamId)
            }
        }
    }

    /** Is Completion Checkers **/
    private fun officialRosterIsComplete() : Boolean {
        realmInstance.ifObjectExists<Roster>(this.officialRosterId) {
            this.officialIsComplete = true
        }
        return officialIsComplete
    }

    private fun tryoutIsComplete() : Boolean {
        realmInstance.ifObjectExists<TryOut>(this.tryoutId) {
            this.tryoutIsComplete = true
        }
        return tryoutIsComplete
    }

    private fun tryoutRosterIsComplete() : Boolean {
        realmInstance.ifObjectExists<Roster>(this.tryoutRosterId) {
            this.tryoutRosterIsComplete = true
        }
        return tryoutRosterIsComplete
    }

    /** CallBacks **/
    private fun teamCallback() : ((teamId:String) -> Unit) {
        return { _ ->
            log("Team Callback")
            pullTeamAndDetailsFromFirebase()
        }
    }
    private fun rosterCallback() : (() -> Unit) {
        return {
            log("Roster Callback")
            for (subscription in rosterSubscriptions) {
                subscription.invoke()
            }
        }
    }
    private fun tryoutCallback() : ((id:String) -> Unit) {
        return { id ->
            log("Tryout Callback")
            // Tryout Roster
            realmInstance.findTryOutById(id)?.let { tryout ->
                tryout.rosterId?.let { rosterId ->
                    this.tryoutRosterId = rosterId
                    if (!tryoutRosterIsComplete()) {
                        fireGetRosterInBackground(rosterId)
                    }
                }
            }
        }
    }

    /** PUSH ROSTERS **/
    fun pushOfficialRosterToFirebase() {
        officialRosterId?.let { itRosterId ->
            realmInstance.findRosterById(itRosterId)?.let { itRoster ->
                val data = itRoster.convertForFirebase()
                firebaseDatabase { itDB ->
                    itDB.child("rosters")
                        .child(itRosterId)
                        .setValue(data)
                }
            }
        }
    }

    fun pushRosterPlayersToFirebase(rosterId: String) {
        realmInstance.findRosterById(rosterId)?.let { itRoster ->
            val data = itRoster.players?.toList()
            firebaseDatabase { itDB ->
                itDB.child("rosters")
                    .child(rosterId)
                    .child("players")
                    .setValue(data)
            }
        }
    }

}

fun Realm.pushPlayersToRosterInFirebase(rosterId: String) {
    this.findRosterById(rosterId)?.let { itRoster ->
        val data = itRoster.players?.toList()
        firebaseDatabase { itDB ->
            itDB.child("rosters")
                .child(rosterId)
                .child("players")
                .setValue(data)
        }
    }
}
fun Realm.pullTeamRosterTryoutFromFirebase(teamId: String) {
    this.findTeamById(teamId)?.let { team ->
        // Official Roster
        team.rosterId?.let { rosterId ->
            this.ifObjectDoesNotExist<Roster>(rosterId) {
                // Pull Roster from Firebase
                fireGetRosterInBackground(rosterId)
            }
        }
        // Tryout
        team.tryoutId?.let { tryoutId ->
            this.ifObjectDoesNotExist<TryOut>(tryoutId) {
                // Pull Tryout From Firebase
                this.fireGetTryOutProfileIntoRealm(tryoutId)
            }
        }
    } ?: run {
        // Pull Team from Firebase
        this.fireGetTeamProfileInBackground(teamId)
    }
}