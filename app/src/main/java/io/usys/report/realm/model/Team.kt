package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.realm.writeToRealm
import io.usys.report.realm.realm
import io.usys.report.realm.session
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */

open class TeamRef : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var teamId: String? = "null"
    var tryoutId: String? = "null"
    var rosterId: String? = "null"
    var name: String? = "null"
    var headCoachId: String? = "null"
    var headCoachName: String? = "null"
    var year: String? = "null"
    var ageGroup: String? = "null"
    var gender: String? = "null"
    var sport: String? = "null"
    var status: String? = "null"
    var imgUrl: String? = "null"
}

open class Team : RealmObject(), Serializable {

    @PrimaryKey
    var id: String = newUUID()
    var headCoachId: String? = "null"
    var headCoachName: String? = "null"
    var coaches: RealmList<CoachRef>? = RealmList()
//    var managers: RealmList<ParentRef>? = RealmList()
//    var organizations: RealmList<OrganizationRef>? = RealmList()
    var rosterId: String? = "null"
    var tryoutId: String? = "null"
    var season: String? = "null"
    var locationIds: RealmList<String>? = null
//    var schedule: Schedule? = null
    var year: String? = "null"
    var ageGroup: String? = "null"
    var isActive: Boolean = false
    var gender: String? = "null"
    var hasReview: Boolean = false
    // base
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
    var name: String? = "null"
    var type: String? = "null"
    var subType: String? = "null"
    var details: String? = "null"
    var isFree: Boolean = false
    var status: String? = "null"
    var mode: String? = "null"
    var imgUrl: String? = "null"
    var sport: String? = "null"
    var chatEnabled: Boolean = false


}
fun Team?.getRosterNoThread(): Roster? {
    if (this.isNullOrEmpty()) return null
    var roster: Roster? = null
    this?.rosterId?.let { rosterId ->
        roster = realm().where(Roster::class.java).equalTo("id", rosterId).findFirst()
    }
    return roster
}

fun Team?.getPlayerFromRosterNoThread(playerId: String): PlayerRef? {
    if (this.isNullOrEmpty()) return null
    var player: PlayerRef? = null
    val roster: Roster? = getRosterNoThread()
    roster?.getPlayer(playerId)?.let {
        player = it
    }
    if (player == null) {
        player = realm().where(PlayerRef::class.java).equalTo("id", playerId).findFirst()
    }
    return player
}

fun executeGetTeamById(teamId:String) : Team? {
    var team: Team? = null
    try {
        writeToRealm {
            team = realm().where(Team::class.java).equalTo("id", teamId).findFirst()
        }
        return team
    } catch (e: Exception) { e.printStackTrace() }
    return team
}

fun getTeamById(teamId:String) : Team? {
    var team: Team? = null
    try {
        session {
            val teams = it.teams
            team = teams?.find {
                it.id == teamId
            }
        }
        if (team == null) {
            team = realm().where(Team::class.java).equalTo("id", teamId).findFirst()
        }
        return team
    } catch (e: Exception) { e.printStackTrace() }
    return team
}