package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import io.usys.report.utils.splitFullName
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */

const val PLAYER_STATUS_OPEN = "open"
const val PLAYER_STATUS_CLOSED = "closed"
const val PLAYER_STATUS_OFFERED = "offered"
const val PLAYER_STATUS_ACCEPTED = "accepted"
const val PLAYER_STATUS_REJECTED = "rejected"
open class PlayerRef : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var playerId: String? = newUUID()
    var name: String? = "unassigned"
    var rank: Int? = 0
    var number: Int = 0
    var tryoutTag: String? = "unassigned"
    var position: String? = "unassigned"
    var foot: String? = "unassigned"
    var dob: String? = "unassigned"
    var imgUrl: String? = "unassigned"
    var pointX: Int? = 0
    var pointY: Int? = 0
    var color: String? = "unassigned"
    var listPosition: Int? = 0
    var status: String? = "unassigned"
    var userId: String? = "unassigned"
    var parentId: String? = "unassigned"
    // play metrics
    var team: String? = "unassigned"
    var season: String? = "unassigned"
    var player_first_name: String? = "unassigned"
    var player_last_name: String? = "unassigned"
    var gender: String? = "unassigned"
    var birth_date: String? = "unassigned"
    var parent1_email: String? = "unassigned"
    var parent1_first_name: String? = "unassigned"
    var parent1_last_name: String? = "unassigned"
    var parent1_mobile_number: String? = "unassigned"
    var parent2_email: String? = "unassigned"
    var parent2_first_name: String? = "unassigned"
    var parent2_last_name: String? = "unassigned"
    var parent2_mobile_number: String? = "unassigned"
    var street: String? = "unassigned"
    var city: String? = "unassigned"
    var state: String? = "unassigned"
    var zip: String? = "unassigned"
}

fun RealmList<PlayerRef>.sortByName(): RealmList<PlayerRef> {
    return this.sortedBy { it.name?.splitFullName()?.second ?: it.name }.toRealmList()
}
fun <T> List<T>.toRealmList(): RealmList<T> {
    val realmList = RealmList<T>()
    realmList.addAll(this)
    return realmList
}
open class Player : RealmObject() {

    @PrimaryKey
    var id: String = newUUID()
    var userId: String? = "unassigned"
    var team: TeamRef? = TeamRef()
    var rank: Int = 0
    var number: Int = 0
    var tryoutTag: String? = "unassigned"
    var notes: RealmList<Note>? = null
    var age: Int = 0
    var dob: String? = "unassigned"
    var position: String? = "unassigned"
    var foot: String? = "unassigned"
    var contacts: RealmList<Contact>? = null
    //Extras
    var playerName: String? = "unassigned"
    var teamName: String? = "unassigned"
    var organizations: RealmList<OrganizationRef>? = null
    var teams: RealmList<TeamRef>? = null
    var hasReview: Boolean = false
    var reviewBundle: ReviewBundle? = null
    // base
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
    var name: String? = "unassigned"
    var firstName: String? = "unassigned"
    var lastName: String? = "unassigned"
    var type: String? = "unassigned"
    var subType: String? = "unassigned"
    var details: String? = "unassigned"
    var isFree: Boolean = false
    var status: String? = "unassigned"
    var mode: String? = "unassigned"
    var imgUrl: String? = "unassigned"
    var sport: String? = "unassigned"

}


//inline fun getPlayerRefsByTeamId(id:String, block: (Roster) -> Unit) {
//    val team = getTeamById(id)
//    team?.rosterId?.let { block(it) }
//}

