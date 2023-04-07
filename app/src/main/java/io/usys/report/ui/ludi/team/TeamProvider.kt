package io.usys.report.ui.ludi.team

import com.google.firebase.database.DatabaseReference
import io.realm.Realm
import io.realm.RealmChangeListener
import io.usys.report.firebase.*
import io.usys.report.realm.model.Team
import io.usys.report.realm.realm
import io.usys.report.utils.log

class RosterProvider {

    val realmInstance = realm()
    var teamIds = mutableListOf<String>()
    var teamListeners = mutableMapOf<String,TeamRealmListener<Team>>()
    var teamListenerUpdates = mutableMapOf<String,((teamId:String) -> Unit)?>()
    var updateCallBack: ((teamId:String) -> Unit)? = null
    var teamReference: DatabaseReference? = null
    var teamFireListener: TeamFireListener


    init {
        firebaseDatabase {
            teamReference = it.child(DatabasePaths.TEAMS.path)
        }
        teamFireListener = TeamFireListener()
    }

    /** FireRoster */
    // PULL
//    fun pullAllTeamsFromFirebase() {
//
//        teamIds.forEach { teamId ->
//
//
//            val updateCallBackTemp = createNewClicker()
//            val teamListenTemp = TeamRealmListener<Team>(teamId = teamId, updateCallBackTemp)
//            teamListenerUpdates[teamId] = updateCallBackTemp
//            teamListeners[teamId] = teamListenTemp
//            realmInstance.fireGetTeamProfileInBackground(teamId)
//        }
//
//
//        realmInstance.fireGetTeamProfilesInBackground(teamIds)
//    }




    // PUSH
    fun pushTeamToFirebase() {
        //todo:
    }

    // SUBSCRIBE
    fun removeRealmRosterListener() {
        realmInstance.removeAllChangeListeners()
    }
    fun subscribeToFirebaseTeamUpdates(teamId: String) {
        teamReference?.child(DatabasePaths.TEAMS.path)
            ?.orderByChild("id")
            ?.equalTo(teamId)
            ?.addValueEventListener(teamFireListener)
    }
    fun removeFireRosterListener() {
        teamReference?.removeEventListener(teamFireListener)
    }

}
class TeamRealmListener<T>(val teamId: String, private val onRealmChange: (teamId:String) -> Unit) : RealmChangeListener<T> {
    override fun onChange(t: T) {
        log("Team listener called")
        val r = t as? Team
        if (teamId != r?.id) return
        onRealmChange(teamId)
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
