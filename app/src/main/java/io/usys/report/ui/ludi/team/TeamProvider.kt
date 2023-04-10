package io.usys.report.ui.ludi.team

import com.google.firebase.database.DatabaseReference
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.usys.report.firebase.*
import io.usys.report.firebase.models.convertForFirebase
import io.usys.report.realm.*
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TryOut
import io.usys.report.ui.ludi.roster.RosterRealmSingleEventListener
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

    var teamListener: TeamRealmSingleEventListener? = null
    var rosterListener: RosterRealmSingleEventListener? = null
    var rosterSubscriptions: MutableList<() -> Unit> = mutableListOf()
    var tryoutListener: TryoutRealmSingleEventListener? = null

    init {
        pullTeamAndDetailsFromFirebase()
        firebaseDatabase {
            teamReference = it.child(DatabasePaths.TEAMS.path)
        }
        teamFireListener = TeamFireListener()
        teamListener = TeamRealmSingleEventListener(teamId = teamId, realmInstance, teamCallback())
        rosterListener = RosterRealmSingleEventListener(rosterCallback())
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
                    tryoutListener = TryoutRealmSingleEventListener(tryoutId = tryoutId, tryoutCallback())
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
            tryoutListener?.unregisterListener()
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
            teamListener?.unregisterListener()
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

}

/** Team Realm Listener */
class TeamRealmSingleEventListener(val teamId: String, val realm: Realm, private val onRealmChange: (teamId: String) -> Unit) : RealmChangeListener<Team> {
    private lateinit var teamResult: Team

    init {
        registerListener()
    }

    override fun onChange(t: Team) {
        log("Team listener called")
        onRealmChange(teamId)
    }

    fun registerListener() {
        teamResult = realm.where(Team::class.java).equalTo("id", teamId).findFirstAsync()
        teamResult.addChangeListener(this)
    }

    fun unregisterListener() {
        teamResult.removeChangeListener(this)
        realm.close()
    }
}

/** TryOut Realm Listener */
class TryoutRealmSingleEventListener(val tryoutId: String, private val onRealmChange: (tryoutId: String) -> Unit) : RealmChangeListener<TryOut> {
    private val realm: Realm = Realm.getDefaultInstance()
    private lateinit var tryoutResult: TryOut

    init {
        registerListener()
    }

    override fun onChange(t: TryOut) {
        log("TryOut listener called")
        onRealmChange(tryoutId)
    }

    fun registerListener() {
        tryoutResult = realm.where(TryOut::class.java).equalTo("id", tryoutId).findFirstAsync()
        tryoutResult.addChangeListener(this)
    }

    fun unregisterListener() {
        tryoutResult.removeChangeListener(this)
        realm.close()
    }
}
inline fun <reified T:RealmObject> Realm.subscribeToUpdates(crossinline updateCallBack: (Realm) -> Unit): RealmChangeListener<T> {
    val listener = RealmChangeListener<T> {
        // Handle changes to the Realm data here
        log("Realm listener called")
        updateCallBack(this)
    }
    this.where(T::class.java)?.findFirstAsync()?.addChangeListener(listener)
    return listener
}
inline fun Realm.subscribeToTryoutUpdates(tryoutId: String, crossinline updateCallBack: (String) -> Unit) {
    val tryoutListener = RealmChangeListener<TryOut> { tryout ->
        // Handle changes to the Realm data here
        log("Roster listener called")
        updateCallBack(tryoutId)
    }
    this.where(TryOut::class.java)?.equalTo("id", tryoutId)?.findFirstAsync()?.addChangeListener(tryoutListener)
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