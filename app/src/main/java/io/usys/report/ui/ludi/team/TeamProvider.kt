package io.usys.report.ui.ludi.team

import com.google.firebase.database.DatabaseReference
import io.realm.Realm
import io.realm.RealmChangeListener
import io.usys.report.firebase.*
import io.usys.report.firebase.models.convertForFirebase
import io.usys.report.realm.*
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TryOut
import io.usys.report.utils.log

class TeamProvider(val teamId: String) {

    val realmInstance = realm()
    var officialRosterId: String? = null
    var tryoutRosterId: String? = null
    var teamReference: DatabaseReference? = null
    var teamFireListener: TeamFireListener

    init {
        realmInstance.findTeamById(teamId)?.let { team ->
            officialRosterId = team.rosterId
            team.tryoutId?.let { tryoutId ->
                realmInstance.findTryOutIdByTeamId(teamId)?.let { tryoutId ->
                    tryoutRosterId = tryoutId
                }
            }
        }
        firebaseDatabase {
            teamReference = it.child(DatabasePaths.TEAMS.path)
        }
        teamFireListener = TeamFireListener()
    }

    /** FireTeam */
    fun pullTeamAndDetailsFromFirebase() {
        realmInstance.findTeamById(teamId)?.let { team ->
            // Official Roster
            team.rosterId?.let { rosterId ->
                this.officialRosterId = rosterId
                fireGetRosterInBackground(rosterId)
            }
            // Tryout
            team.tryoutId?.let { tryoutId ->
                TryoutRealmSingleEventListener(tryoutId = tryoutId, tryoutCallback())
                realmInstance.fireGetTryOutProfileIntoRealm(tryoutId)
            }
        } ?: run {
            // Team
            realmInstance.ifObjectDoesNotExist<Team>(teamId) {
                TeamRealmSingleEventListener(teamId = teamId, teamCallback())
                realmInstance.fireGetTeamProfileInBackground(teamId)
            }
        }
    }

    private fun teamCallback() : ((teamId:String) -> Unit) {
        return { teamId ->
            log("Team Updated")
            pullTeamAndDetailsFromFirebase()
        }
    }

    private fun tryoutCallback() : ((id:String) -> Unit) {
        return { id ->
            log("Tryout Updated")
            // Tryout Roster
            realmInstance.findTryOutById(id)?.let { tryout ->
                tryout.rosterId?.let { rosterId ->
                    this.tryoutRosterId = rosterId
                    fireGetRosterInBackground(rosterId)
                }
            }
        }
    }

    // PULL
    fun pullRostersFromFirebase() {
        officialRosterId?.let {
            fireGetRosterInBackground(it)
        }
        tryoutRosterId?.let {
            fireGetRosterInBackground(it)
        }
    }

    // PUSH
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

}
class TeamRealmSingleEventListener(val teamId: String, private val onRealmChange: (teamId: String) -> Unit) : RealmChangeListener<Team> {
    private val realm: Realm = Realm.getDefaultInstance()
    private lateinit var teamResult: Team

    init {
        registerListener()
    }

    override fun onChange(t: Team) {
        log("Team listener called")
//        val r = t as? Team
//        if (teamId != r?.id) returnx
//        unregisterListener()
        onRealmChange(teamId)
    }

    private fun registerListener() {
        teamResult = realm.where(Team::class.java).equalTo("id", teamId).findFirstAsync()
        teamResult.addChangeListener(this)
    }

    private fun unregisterListener() {
        teamResult.removeChangeListener(this)
        realm.close()
    }
}


class TryoutRealmSingleEventListener(val tryoutId: String, private val onRealmChange: (tryoutId: String) -> Unit) : RealmChangeListener<TryOut> {
    private val realm: Realm = Realm.getDefaultInstance()
    private lateinit var tryoutResult: TryOut

    init {
        registerListener()
    }

    override fun onChange(t: TryOut) {
        log("Team listener called")
        unregisterListener()
        onRealmChange(tryoutId)
    }

    private fun registerListener() {
        tryoutResult = realm.where(TryOut::class.java).equalTo("id", tryoutId).findFirstAsync()
        tryoutResult.addChangeListener(this)
    }

    private fun unregisterListener() {
        tryoutResult.removeChangeListener(this)
        realm.close()
    }
}
inline fun Realm.subscribeToTeamUpdates(teamId: String? = null, crossinline updateCallBack: (String) -> Unit) {
    val teamListener = RealmChangeListener<Team> { team ->
        // Handle changes to the Realm data here
        log("Roster listener called")
        updateCallBack(team.id)
    }
    this.where(Team::class.java)?.equalTo("id", teamId)?.findFirstAsync()?.addChangeListener(teamListener)
}
