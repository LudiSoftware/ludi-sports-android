package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID

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




