package io.usys.report.realm.model.users

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID

open class Player : RealmObject() {

    @PrimaryKey
    var id: String = newUUID()
    var userId: String? = "unassigned"
    var team: String? = ""
    var rank: Int = 0
    var number: Int = 0
    var tryoutTag: String? = "unassigned"
    var notes: RealmList<String>? = null
    var age: Int = 0
    var dob: String? = "unassigned"
    var position: String? = "unassigned"
    var foot: String? = "unassigned"
    var contacts: RealmList<String>? = null
    //Extras
    var playerName: String? = "unassigned"
    var teamName: String? = "unassigned"
    var organizations: RealmList<String>? = null
    var teams: RealmList<String>? = null
    var hasReview: Boolean = false
    var reviewIds: RealmList<String>? = null
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




