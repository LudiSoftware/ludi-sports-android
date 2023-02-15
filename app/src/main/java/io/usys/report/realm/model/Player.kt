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

open class GenericPlayer : RealmObject(), Serializable {
    @PrimaryKey
    var playerId: String = newUUID()
    var teamId: String = ""
    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var name: String = ""
    var rank: Int = 0
    var number: Int = 0
    var tryoutTag: String = ""
    var notes: RealmList<CoachNote>? = null
    var status: String = ""
    var age: Int = 0
    var dob: String = ""
    var position: String = ""
    var imgUrl: String = ""

}
open class Player : RealmObject(), Serializable {

    companion object {
        const val ORDER_BY_ORGANIZATION = "organizationId"
    }

    var ownerId: String = ""
    var ownerName: String = ""
    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var name: String = "" //Name Given by Manager
    var firstName: String = ""
    var lastName: String = ""
    var age: Int = 0
    var position: String = ""
    var imgUrl: String = ""
    var teamId: String = ""
    var teamName: String = ""
    var organizationId: String? = null
    var organizationName: String? = null
    var organizationIds: RealmList<String>? = null
    var sport: String = "unassigned"
    var type: String = "unassigned"
    var subType: String = "unassigned"
    var details: String = ""
    var email: String = ""
    var phone: String = ""
    var isFree: Boolean = false
    var teams: RealmList<String>? = null
    var notes: RealmList<CoachNote>? = null
    var hasReview: Boolean = false
    var reviews: RealmList<String>? = null
    var ratingScore: String = "0"
    var ratingCount: String = "0"
    var reviewAnswerCount: String = "0"
    var reviewDetails: String? = null

}




