package io.usys.report.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.applyAndFireSave
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */
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
    var organizationId: String = ""
    var organizationName: String = ""
    var organizationIds: RealmList<String>? = RealmList()
    var sport: String = "unassigned"
    var type: String = "unassigned"
    var subType: String = "unassigned"
    var details: String = ""
    var email: String = ""
    var phone: String = ""
    var isFree: Boolean = false
    var teams: RealmList<String>? = null

    var hasReview: Boolean = false
    var reviews: RealmList<String>? = RealmList()
    var ratingScore: String = ""
    var ratingCount: String = ""
    var reviewAnswerCount: String = ""
    var reviewDetails: String = ""

}




