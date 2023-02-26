package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */

open class PlayerRef : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var playerId: String? = newUUID()
    var name: String? = "null"
    var rank: Int? = 0
    var number: Int = 0
    var tryoutTag: String? = "unassigned"
    var position: String? = "unassigned"
    var foot: String? = "right"
    var dob: String? = "null"
    var imgUrl: String? = "null"
    var pointX: Int? = 0
    var pointY: Int? = 0

}

open class Player : RealmObject() {

    @PrimaryKey
    var id: String = newUUID()
    var team: TeamRef? = TeamRef()
    var rank: Int = 0
    var number: Int = 0
    var tryoutTag: String? = null
    var notes: RealmList<Note>? = null
    var age: Int = 0
    var dob: String? = null
    var position: String? = null
    var foot: String? = null
    var contacts: RealmList<Contact>? = null
    var evaluations: RealmList<PlayerEvaluationRef>? = null
    //Extras
    var playerName: String? = null
    var teamName: String? = null
    var organizations: RealmList<OrganizationRef>? = null
    var teams: RealmList<TeamRef>? = null
    var hasReview: Boolean = false
    var reviewBundle: ReviewBundle? = null
    // base
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
    var name: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var type: String? = null
    var subType: String? = null
    var details: String? = null
    var isFree: Boolean = false
    var status: String? = null
    var mode: String? = null
    var imgUrl: String? = null
    var sport: String? = null

}


inline fun getPlayerRefsByTeamId(id:String, block: (Roster) -> Unit) {
    val team = getTeamById(id)
    team?.roster?.let { block(it) }
}

fun getPlayerRefsByTeamId(teamId:String, playerId:String): PlayerRef? {
    val team = getTeamById(teamId)
    return team?.roster?.players?.find { it.id == playerId }
}
