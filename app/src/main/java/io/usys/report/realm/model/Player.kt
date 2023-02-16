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



open class Player : RealmObject(), Serializable {

    @PrimaryKey
    var playerId: String = newUUID()
    var teamId: TeamRef? = TeamRef()
    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var name: String? = ""
    var rank: Int = 0
    var number: Int = 0
    var tryoutTag: String? = ""
    var notes: RealmList<CoachNote>? = null
    var status: String? = ""
    var age: Int = 0
    var dob: String? = ""
    var position: String? = ""
    var imgUrl: String? = ""
    //Extras
    var ownerId: String? = ""
    var ownerName: String? = ""
    var firstName: String? = ""
    var lastName: String? = ""
    var teamName: String? = ""
    var organizationId: String? = null
    var organizationName: String? = null
    var organizationIds: RealmList<String>? = null
    var sport: String? = null
    var type: String? = null
    var subType: String? = null
    var details: String? = null
    var isFree: Boolean? = false
    var teamIds: RealmList<String>? = null
    var hasReview: Boolean = false
    var reviewBundle: ReviewBundle? = null

}




